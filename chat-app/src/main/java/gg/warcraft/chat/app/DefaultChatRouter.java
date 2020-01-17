package gg.warcraft.chat.app;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import gg.warcraft.chat.api.ChatRouter;
import gg.warcraft.chat.api.PriorityChatListener;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.channel.service.ChannelQueryService;
import gg.warcraft.chat.api.profile.ChatProfile;
import gg.warcraft.chat.api.profile.service.ChatProfileQueryService;
import gg.warcraft.chat.app.channel.handler.ChannelCommandHandler;
import gg.warcraft.chat.app.event.NativeAsyncPlayerChatEvent;
import gg.warcraft.monolith.api.core.TaskService;
import gg.warcraft.monolith.api.core.command.Command;
import gg.warcraft.monolith.api.core.command.CommandHandler;
import gg.warcraft.monolith.api.core.command.CommandService;
import gg.warcraft.monolith.api.core.command.PlayerCommandSender;
import gg.warcraft.monolith.api.core.event.Event;
import gg.warcraft.monolith.api.core.event.EventService;
import gg.warcraft.monolith.api.entity.player.Player;
import gg.warcraft.monolith.api.entity.player.service.PlayerQueryService;
import scala.Option$;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class DefaultChatRouter implements ChatRouter {
    private static final String HOME_CHANNEL_MISSING = "Failed to retrieve home channel for player with id %s";

    private final ChannelQueryService channelQueryService;
    private final ChatProfileQueryService profileQueryService;
    private final CommandService commandService;
    private final PlayerQueryService playerQueryService;
    private final TaskService taskService;

    private final ChannelCommandHandler commandHandler = new ChannelCommandHandler();

    final Map<UUID, PriorityChatListener> priorityListeners;

    @Inject
    public DefaultChatRouter(ChannelQueryService channelQueryService, ChatProfileQueryService profileQueryService,
                             CommandService commandService, EventService eventService, PlayerQueryService playerQueryService,
                             TaskService taskService) {
        this.channelQueryService = channelQueryService;
        this.profileQueryService = profileQueryService;
        this.commandService = commandService;
        this.playerQueryService = playerQueryService;
        this.taskService = taskService;
        this.priorityListeners = new HashMap<>();
    }

    @Override
    public void registerPriorityListener(UUID playerId, PriorityChatListener listener) {
        priorityListeners.put(playerId, listener);
    }

    @Override
    public void handle(Event event) {
        if (event instanceof NativeAsyncPlayerChatEvent) {
            onNativeAsyncPlayerChatEvent((NativeAsyncPlayerChatEvent) event);
        }
    }

    public void onNativeAsyncPlayerChatEvent(NativeAsyncPlayerChatEvent event) {
        PriorityChatListener priorityListener = priorityListeners.remove(event.getPlayerId());
        if (priorityListener != null) {
            // run on next sync tick as chat events are async
            taskService.runNextTick(() -> priorityListener.onChat(event.getPlayerId(), event.getText()));
            return;
        }

        Channel channel = channelQueryService.findChannelWithMatchingShortcut(event.getText());
        String text;
        if (channel == null) {
            ChatProfile profile = profileQueryService.getChatProfile(event.getPlayerId());
            channel = channelQueryService.getChannelByAlias(profile.getHomeChannel());
            if (channel == null) {
                throw new IllegalStateException(String.format(HOME_CHANNEL_MISSING, event.getPlayerId()));
            }
            text = event.getText();
        } else {
            text = event.getText().substring(channel.getShortcut().length()).trim();
        }

        String command = channel.getName().toLowerCase();
        CommandHandler handler = commandHandler.getHandler(command);
        if (handler != null) {
            Player player = playerQueryService.getPlayer(event.getPlayerId());
            handler.handle(
                    new PlayerCommandSender(
                            player.getName(),
                            Option$.MODULE$.apply(event.getPlayerId())
                    ),
                    new Command(command, command,
                            scala.collection.JavaConverters.collectionAsScalaIterableConverter(
                                    Arrays.asList(text.split(" "))
                            ).asScala().toSeq())
            );
        }
    }
}
