package gg.warcraft.chat.app.channel.handler;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import gg.warcraft.chat.api.message.MessageCommandService;
import gg.warcraft.chat.api.message.MessageFactory;
import gg.warcraft.chat.api.profile.service.ChatProfileCommandService;
import gg.warcraft.chat.api.profile.service.ChatProfileQueryService;
import gg.warcraft.chat.app.MessageFormatter;
import gg.warcraft.chat.app.channel.LocalChannel;
import gg.warcraft.chat.app.logger.MessageLogger;
import gg.warcraft.monolith.api.command.Command;
import gg.warcraft.monolith.api.command.CommandHandler;
import gg.warcraft.monolith.api.command.CommandSender;
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
        var nearbyEntities = entityQueryService.getNearbyEntities(location, channel.getRadius());
        return nearbyEntities.stream()
                .filter(entity -> entity instanceof Player)
                .map(Entity::getId)
                .collect(Collectors.toList());
    }

    boolean onPlayerChatCommand(CommandSender sender, String text) {
        var senderProfile = sender.isPlayer()
                ? profileQueryService.getChatProfile(sender.getPlayerId())
                : profileQueryService.getConsoleChatProfile();
        var formattedText = formatter.format(channel, senderProfile, text);
        var message = messageFactory.createMessage(channel, sender, text, formattedText);
        var player = playerQueryService.getPlayer(sender.getPlayerId());
        var recipientIds = getNearbyPlayerIds(player.getLocation());
        recipientIds.forEach(recipient -> messageCommandService.sendMessageToPlayer(message, recipient));
        messageCommandService.sendMessageToPlayer(message, sender.getPlayerId()); // FIXME this currently masks an issue with getNearbyPlayerIds, remove this when Local channels properly send messages to nearby players
        if (recipientIds.size() == 1) {
            var muteMessage = messageFactory.createMuteMessage();
            messageCommandService.sendMessageToPlayer(muteMessage, sender.getPlayerId());
        }

        logger.log(message);
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, List<String> arguments) {
        if (!sender.isPlayer()) {
            var playersOnlyMessage = messageFactory.createServerMessage(PLAYERS_ONLY);
            messageCommandService.sendMessageToConsole(playersOnlyMessage);
            return true;
        }

        if (arguments.isEmpty()) {
            profileCommandService.setHomeChannel(sender.getPlayerId(), channel);
            return true;
        }

        var text = String.join(" ", arguments);
        return onPlayerChatCommand(sender, text);
    }
}
