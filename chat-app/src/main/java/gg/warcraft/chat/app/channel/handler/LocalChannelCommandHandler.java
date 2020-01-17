package gg.warcraft.chat.app.channel.handler;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import gg.warcraft.chat.api.message.Message;
import gg.warcraft.chat.api.message.MessageCommandService;
import gg.warcraft.chat.api.message.MessageFactory;
import gg.warcraft.chat.api.profile.ChatProfile;
import gg.warcraft.chat.api.profile.service.ChatProfileCommandService;
import gg.warcraft.chat.api.profile.service.ChatProfileQueryService;
import gg.warcraft.chat.app.MessageFormatter;
import gg.warcraft.chat.app.channel.LocalChannel;
import gg.warcraft.chat.app.logger.MessageLogger;
import gg.warcraft.monolith.api.core.command.Command;
import gg.warcraft.monolith.api.core.command.CommandHandler;
import gg.warcraft.monolith.api.core.command.CommandSender;
import gg.warcraft.monolith.api.entity.Entity;
import gg.warcraft.monolith.api.entity.player.Player;
import gg.warcraft.monolith.api.entity.player.service.PlayerQueryService;
import gg.warcraft.monolith.api.entity.service.EntityQueryService;
import gg.warcraft.monolith.api.world.Location;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LocalChannelCommandHandler implements CommandHandler {
    private static final String PLAYERS_ONLY = "Only players can talk in local chat channels.";

    private final ChatProfileCommandService profileCommandService;
    private final ChatProfileQueryService profileQueryService;
    private final MessageFactory messageFactory;
    private final MessageCommandService messageCommandService;
    private final MessageLogger logger;
    private final EntityQueryService entityQueryService;
    private final PlayerQueryService playerQueryService;
    private final LocalChannel channel;

    final MessageFormatter formatter;

    @Inject
    public LocalChannelCommandHandler(ChatProfileCommandService profileCommandService,
                                      ChatProfileQueryService profileQueryService, MessageFactory messageFactory,
                                      MessageCommandService messageCommandService, MessageLogger logger,
                                      EntityQueryService entityQueryService, PlayerQueryService playerQueryService,
                                      @Assisted LocalChannel channel) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.messageFactory = messageFactory;
        this.messageCommandService = messageCommandService;
        this.logger = logger;
        this.entityQueryService = entityQueryService;
        this.playerQueryService = playerQueryService;
        this.channel = channel;
        this.formatter = new MessageFormatter(channel.getFormattingString());
    }

    Collection<UUID> getNearbyPlayerIds(Location location) {
        List<Entity> nearbyEntities = entityQueryService.getNearbyEntities(location, channel.getRadius());
        return nearbyEntities.stream()
                .filter(entity -> entity instanceof Player)
                .map(Entity::getId)
                .collect(Collectors.toList());
    }

    boolean onPlayerChatCommand(CommandSender sender, String text) {
        ChatProfile senderProfile = sender.isPlayer()
                ? profileQueryService.getChatProfile(sender.playerId().get())
                : profileQueryService.getConsoleChatProfile();
        String formattedText = formatter.format(channel, senderProfile, text);
        Message message = messageFactory.createMessage(channel, sender, text, formattedText);
        Player player = playerQueryService.getPlayer(sender.playerId().get());
        Collection<UUID> recipientIds = getNearbyPlayerIds(player.getLocation());
        recipientIds.forEach(recipient -> messageCommandService.sendMessageToPlayer(message, recipient));
        if (recipientIds.size() == 1) {
            Message muteMessage = messageFactory.createMuteMessage();
            messageCommandService.sendMessageToPlayer(muteMessage, sender.playerId().get());
        }

        logger.log(message);
        return true;
    }

    @Override
    public boolean handle(CommandSender sender, Command command) {
        if (!sender.isPlayer()) {
            Message playersOnlyMessage = messageFactory.createServerMessage(PLAYERS_ONLY);
            messageCommandService.sendMessageToConsole(playersOnlyMessage);
            return true;
        }

        if (command.args().isEmpty()) {
            profileCommandService.setHomeChannel(sender.playerId().get(), channel);
            return true;
        }

        String text = command.args().mkString(" ");
        return onPlayerChatCommand(sender, text);
    }
}
