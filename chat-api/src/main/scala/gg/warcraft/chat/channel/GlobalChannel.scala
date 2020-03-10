package gg.warcraft.chat.channel

import java.util.UUID

import gg.warcraft.chat.message.{Message, MessageAdapter}
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.monolith.api.core.command.{Command, CommandSender}
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.entity.player.Player
import gg.warcraft.monolith.api.entity.player.service.PlayerQueryService
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
    format: String,
    permission: Option[String]
)(
    private implicit val playerService: PlayerQueryService,
    override protected implicit val profileService: ChatProfileService,
    override protected implicit val messageAdapter: MessageAdapter
) extends Channel
    with EventHandler {
  private final val missingPermissions =
    "You do not have the required permissions to join [%s]."
  private final val successfullyJoined =
    "You have joined [%s]."
  private final val homeChannelPlayersOnly =
    "Only players can set their home channel."

  private val recipients = mutable.ListBuffer[UUID]()

  override def handle(sender: CommandSender, cmd: Command): Boolean = sender match {
    case CommandSender(_, Some(playerId)) =>
      val joined = if (!recipients.contains(playerId)) {
        permission match {
          case Some(permission) =>
            val player: Player = playerService.getPlayer(playerId)
            if (player.hasPermission(permission)) {
              recipients.addOne(playerId)
              val message = Message.server(successfullyJoined.format(name))
              messageAdapter.send(message, playerId)
              true
            } else {
              val message = Message.server(missingPermissions.format(name))
              messageAdapter.send(message, playerId)
              false
            }

          case _ =>
            recipients.addOne(playerId)
            val message = Message.server(successfullyJoined.format(name))
            messageAdapter.send(message, playerId)
            true
        }
      } else true

      if (joined) {
        if (cmd.args.isEmpty) makeHome(playerId)
        else broadcast(sender, cmd.args.mkString(" "), recipients)
      }

      true

    case _ =>
      if (cmd.args.isEmpty) println(homeChannelPlayersOnly)
      else broadcast(sender, cmd.args.mkString(" "), recipients)
      true
  }

  override def handle(event: Event): Unit = event match {
    case it: PlayerConnectEvent            => handle(it)
    case it: PlayerPermissionsChangedEvent => handle(it)
    case it: PlayerDisconnectEvent         => handle(it)
    case _                                 => ()
  }

  private def handle(event: PlayerConnectEvent): Unit = {
    import event.playerId

    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(playerId)
        if (player.hasPermission(permission)) recipients.addOne(playerId)

      case None => recipients.addOne(playerId)
    }
  }

  private def handle(event: PlayerPermissionsChangedEvent): Unit = {
    import event.playerId

    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(playerId)
        if (recipients.contains(playerId)) {
          if (!player.hasPermission(permission)) recipients.subtractOne(playerId)
        } else {
          if (player.hasPermission(permission)) recipients.addOne(playerId)
        }

      case None => ()
    }
  }

  private def handle(event: PlayerDisconnectEvent): Unit = {
    import event.playerId

    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(playerId)
        if (player.hasPermission(permission)) recipients.subtractOne(playerId)

      case None => recipients.subtractOne(playerId)
    }
  }
}
