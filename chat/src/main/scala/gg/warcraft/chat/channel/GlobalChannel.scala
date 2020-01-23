package gg.warcraft.chat.channel

import java.util.UUID

import gg.warcraft.monolith.api.core.command.{Command, CommandSender}
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.entity.player.Player
import gg.warcraft.monolith.api.player.{
  PlayerConnectEvent, PlayerDisconnectEvent, PlayerPermissionsChangedEvent
}
import gg.warcraft.monolith.api.util.ColorCode

import scala.collection.mutable

case class GlobalChannel(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode,
    formatString: String,
    permission: Option[String]
) extends Channel
    with EventHandler {
  private final val missingPermissions =
    "You do not have the required permissions to join [%s]."
  private final val successfullyJoined =
    "You have joined [%s]."
  private final val homeChannelPlayersOnly =
    "Only players can set their home channel."

  private val recipients = mutable.ListBuffer[UUID]()

  /*
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
   */

  override def handle(sender: CommandSender, cmd: Command): Boolean = sender match {
    case CommandSender(_, Some(playerId)) =>
      val joined = if (!recipients.contains(playerId)) {
        permission match {
          case Some(permission) =>
            val player: Player = null // TODO PlayerService.getPlayer(event.playerId)
            if (player.hasPermission(permission)) {
              recipients += playerId
              val message = successfullyJoined.format(name)
              // Message joinedMessage = messageFactory.createServerMessage(joined);
              // messageCommandService.sendMessageToPlayer(joinedMessage, sender.playerId().get());
              true
            } else {
              val message = missingPermissions.format(name)
              // Message missingPermissionsMessage = messageFactory.createServerMessage(missingPermissions);
              // messageCommandService.sendMessageToPlayer(missingPermissionsMessage, sender.playerId().get());
              false
            }
          case _ =>
            recipients += playerId
            val message = successfullyJoined.format(name)
            // Message joinedMessage = messageFactory.createServerMessage(joined);
            // messageCommandService.sendMessageToPlayer(joinedMessage, sender.playerId().get());
            true
        }
      } else true

      if (joined) {
        if (cmd.args.isEmpty) {
          // profileCommandService.setHomeChannel(sender.playerId().get(), channel)
          true
        } else {
          //String text = command.args().mkString(" ");
          // onChatCommand(sender, text);
          true
        }
      } else false

    case _ =>
      if (cmd.args.isEmpty) {
//        Message homeChannelPlayersOnly = messageFactory.createServerMessage(HOME_CHANNEL_PLAYERS_ONLY);
//        messageCommandService.sendMessageToConsole(homeChannelPlayersOnly);
        true;
      } else {
//        String text = command.args().mkString(" ");
//        onChatCommand(sender, text);
        true
      }
  }

  override def handle(event: Event): Unit = event match {
    case event: PlayerConnectEvent =>
      permission match {
        case Some(permission) =>
          val player: Player = null // TODO PlayerService.getPlayer(event.playerId)
          if (player.hasPermission(permission)) recipients += event.playerId
        case _ => recipients += event.playerId
      }

    case event: PlayerPermissionsChangedEvent =>
      permission match {
        case Some(permission) =>
          val player: Player = null // TODO PlayerService.getPlayer(event.playerId)
          if (recipients.contains(event.playerId)) {
            if (!player.hasPermission(permission)) recipients -= event.playerId
          } else {
            if (player.hasPermission(permission)) recipients += event.playerId
          }
        case _ => ()
      }

    case event: PlayerDisconnectEvent =>
      permission match {
        case Some(permission) =>
          val player: Player = null // TODO PlayerService.getPlayer(event.playerId)
          if (player.hasPermission(permission)) recipients -= event.playerId
        case _ => recipients -= event.playerId
      }
  }
}
