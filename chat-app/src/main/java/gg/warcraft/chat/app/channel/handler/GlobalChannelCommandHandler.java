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
import gg.warcraft.chat.app.channel.GlobalChannel;
import gg.warcraft.chat.app.logger.MessageLogger;
import gg.warcraft.monolith.api.core.command.Command;
import gg.warcraft.monolith.api.core.command.CommandHandler;
import gg.warcraft.monolith.api.core.command.CommandSender;

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
        ChatProfile senderProfile = sender.isPlayer()
                ? profileQueryService.getChatProfile(sender.playerId().get())
                : profileQueryService.getConsoleChatProfile();
        String formattedText = formatter.format(channel, senderProfile, text);
        Message message = messageFactory.createMessage(channel, sender, text, formattedText);

        channel.getRecipients().forEach(playerId -> messageCommandService.sendMessageToPlayer(message, playerId));
        if (channel.getRecipients().size() == 1) {
            Message muteMessage = messageFactory.createMuteMessage();
            if (sender.isPlayer()) {
                messageCommandService.sendMessageToPlayer(muteMessage, sender.playerId().get());
            } else {
                messageCommandService.sendMessageToConsole(muteMessage);
            }
        }

        logger.log(message);
        return true;
    }

    @Override
    public boolean handle(CommandSender sender, Command command) {
        if (sender.isPlayer()) {
            if (channel.getRecipients().contains(sender.playerId().get())) {
                if (command.args().isEmpty()) {
                    profileCommandService.setHomeChannel(sender.playerId().get(), channel);
                    return true;
                }
            } else {
                if (command.args().isEmpty()) {
                    if (channel.getJoinCondition().test(sender.playerId().get())) {
                        // TODO add player to channel recipients
                        String joined = String.format(JOINED, channel.getName());
                        Message joinedMessage = messageFactory.createServerMessage(joined);
                        messageCommandService.sendMessageToPlayer(joinedMessage, sender.playerId().get());
                        return true;
                    } else {
                        String missingPermissions = String.format(MISSING_PERMISSIONS, channel.getName());
                        Message missingPermissionsMessage = messageFactory.createServerMessage(missingPermissions);
                        messageCommandService.sendMessageToPlayer(missingPermissionsMessage, sender.playerId().get());
                        return true;
                    }
                } else {
                    String notJoined = String.format(NOT_JOINED, channel.getName());
                    Message notJoinedMessage = messageFactory.createServerMessage(notJoined);
                    messageCommandService.sendMessageToPlayer(notJoinedMessage, sender.playerId().get());
                    return true;
                }
            }
        } else {
            if (command.args().isEmpty()) {
                Message homeChannelPlayersOnly = messageFactory.createServerMessage(HOME_CHANNEL_PLAYERS_ONLY);
                messageCommandService.sendMessageToConsole(homeChannelPlayersOnly);
                return true;
            }
        }

        String text = command.args().mkString(" ");
        return onChatCommand(sender, text);
    }
}
