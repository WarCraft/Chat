package gg.warcrat.chat.app;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import gg.warcraft.monolith.api.chat.ChatRouter;
import gg.warcraft.monolith.api.chat.PriorityChatListener;
import gg.warcraft.monolith.api.chat.channel.Channel;
import gg.warcraft.monolith.api.chat.channel.ChannelQueryService;
import gg.warcraft.monolith.api.chat.profile.ChatProfile;
import gg.warcraft.monolith.api.chat.profile.service.ChatProfileQueryService;
import gg.warcraft.monolith.api.command.service.CommandCommandService;
import gg.warcraft.monolith.api.core.TaskService;
import gg.warcraft.monolith.api.event.EventService;
import gg.warcraft.monolith.app.chat.event.NativeAsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class DefaultChatRouter implements ChatRouter {
    private static final String HOME_CHANNEL_MISSING = "Failed to retrieve home channel for player with id %s";

    private final ChannelQueryService channelQueryService;
    private final ChatProfileQueryService profileQueryService;
    private final CommandCommandService commandCommandService;
    private final TaskService taskService;

    final Map<UUID, PriorityChatListener> priorityListeners;

    @Inject
    public DefaultChatRouter(ChannelQueryService channelQueryService, ChatProfileQueryService profileQueryService,
                             CommandCommandService commandCommandService, EventService eventService,
                             TaskService taskService) {
        this.channelQueryService = channelQueryService;
        this.profileQueryService = profileQueryService;
        this.commandCommandService = commandCommandService;
        this.taskService = taskService;
        this.priorityListeners = new HashMap<>();
        eventService.subscribe(this);
    }

    @Override
    public void registerPriorityListener(UUID playerId, PriorityChatListener listener) {
        priorityListeners.put(playerId, listener);
    }

    @Subscribe
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
        String command = String.format("%s %s", channel.getName(), text);
        commandCommandService.dispatchCommandFor(command, event.getPlayerId());
    }
}
