package gg.warcraft.chat.app.channel.handler;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import gg.warcraft.chat.api.message.MessageCommandService;
import gg.warcraft.chat.api.message.MessageFactory;
import gg.warcraft.chat.api.profile.service.ChatProfileCommandService;
import gg.warcraft.chat.api.profile.service.ChatProfileQueryService;
import gg.warcraft.chat.app.MessageFormatter;
import gg.warcraft.chat.app.channel.GlobalChannel;
import gg.warcraft.chat.app.logger.MessageLogger;
import gg.warcraft.monolith.api.command.Command;
import gg.warcraft.monolith.api.command.CommandHandler;
import gg.warcraft.monolith.api.command.CommandSender;

import java.util.List;

public class GlobalChannelCommandHandler implements CommandHandler {
    private static final String MISSING_PERMISSIONS = "You do not have the required permissions to join the %s channel.";
    private static final String JOINED = "You have joined the %s channel.";
    private static final String NOT_JOINED = "You have not joined the %s channel. You can attempt to do so by sending it an empty message.";
    private static final String HOME_CHANNEL_PLAYERS_ONLY = "Only players can set their home channel.";

    private final ChatProfileCommandService profileCommandService;
    private final ChatProfileQueryService profileQueryService;
    private final MessageFactory messageFactory;
    private final MessageCommandService messageCommandService;
    private final MessageLogger logger;
    private final GlobalChannel channel;

    final MessageFormatter formatter;

    @Inject
    public GlobalChannelCommandHandler(ChatProfileCommandService profileCommandService,
                                       ChatProfileQueryService profileQueryService, MessageFactory messageFactory,
                                       MessageCommandService messageCommandService, MessageLogger logger,
                                       @Assisted GlobalChannel channel) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.messageFactory = messageFactory;
        this.messageCommandService = messageCommandService;
        this.logger = logger;
        this.channel = channel;
        this.formatter = new MessageFormatter(channel.getFormattingString());
    }

    boolean onChatCommand(CommandSender sender, String text) {
        var senderProfile = sender.isPlayer()
                ? profileQueryService.getChatProfile(sender.getPlayerId())
                : profileQueryService.getConsoleChatProfile();
        var formattedText = formatter.format(channel, senderProfile, text);
        var message = messageFactory.createMessage(channel, sender, text, formattedText);

        channel.getRecipients().forEach(playerId -> messageCommandService.sendMessageToPlayer(message, playerId));
        if (channel.getRecipients().size() == 1) {
            var muteMessage = messageFactory.createMuteMessage();
            if (sender.isPlayer()) {
                messageCommandService.sendMessageToPlayer(muteMessage, sender.getPlayerId());
            } else {
                messageCommandService.sendMessageToConsole(muteMessage);
            }
        }

        logger.log(message);
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, List<String> arguments) {
        if (sender.isPlayer()) {
            if (channel.getRecipients().contains(sender.getPlayerId())) {
                if (arguments.isEmpty()) {
                    profileCommandService.setHomeChannel(sender.getPlayerId(), channel);
                    return true;
                }
            } else {
                if (arguments.isEmpty()) {
                    if (channel.getJoinCondition().test(sender.getPlayerId())) {
                        // TODO add player to channel recipients
                        var joined = String.format(JOINED, channel.getName());
                        var joinedMessage = messageFactory.createServerMessage(joined);
                        messageCommandService.sendMessageToPlayer(joinedMessage, sender.getPlayerId());
                        return true;
                    } else {
                        var missingPermissions = String.format(MISSING_PERMISSIONS, channel.getName());
                        var missingPermissionsMessage = messageFactory.createServerMessage(missingPermissions);
                        messageCommandService.sendMessageToPlayer(missingPermissionsMessage, sender.getPlayerId());
                        return true;
                    }
                } else {
                    var notJoined = String.format(NOT_JOINED, channel.getName());
                    var notJoinedMessage = messageFactory.createServerMessage(notJoined);
                    messageCommandService.sendMessageToPlayer(notJoinedMessage, sender.getPlayerId());
                    return true;
                }
            }
        } else {
            if (arguments.isEmpty()) {
                var homeChannelPlayersOnly = messageFactory.createServerMessage(HOME_CHANNEL_PLAYERS_ONLY);
                messageCommandService.sendMessageToConsole(homeChannelPlayersOnly);
                return true;
            }
        }

        var text = String.join(" ", arguments);
        return onChatCommand(sender, text);
    }
}
