package gg.warcraft.chat.channel

import java.util.UUID
import java.util.logging.Logger

import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.api.core.{ColorCode, Message}
import gg.warcraft.monolith.api.core.auth.Principal
import gg.warcraft.monolith.api.core.command.Command
import gg.warcraft.monolith.api.core.event.Event
import gg.warcraft.monolith.api.player.{
  Player, PlayerConnectEvent, PlayerDisconnectEvent, PlayerPermissionsChangedEvent,
  PlayerService
}

case class GlobalChannel(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode.Type,
    format: String,
    permission: Option[String]
)(implicit
  logger: Logger,
  playerService: PlayerService,
  profileService: ProfileService,
  messageAdapter: MessageAdapter
) extends Channel
    with Event.Handler {
  private final val missingPermissions =
    "You do not have the required permissions to join [%s]."
  private final val successfullyJoined =
    "You have joined [%s]."
  private final val homeChannelPlayersOnly =
    "Only players can set their home channel."

  private var recipients: Set[UUID] = Set.empty

  private def authenticatePlayer(principal: Principal): Boolean = {
    val playerId = principal.principalId.get

    def fixMissing(): Unit = {
      logger warning s"$playerId was not in $name, but channel has no permission!"
      recipients += playerId
      val message = Message.server(successfullyJoined.format(name))
      messageAdapter.send(message, playerId)
    }

    if (!recipients.contains(playerId)) {
      permission match {
        case Some(permission) =>
          if (principal.hasPermission(permission)) {
            fixMissing(); true
          } else {
            val message = Message.server(missingPermissions.format(name))
            messageAdapter.send(message, playerId); false
          }

        case _ => fixMissing(); true
      }
    } else true
  }

  override def handle(
      principal: Principal,
      command: Command,
      args: String*
  ): Command.Result = principal.principalId match {
    case Some(playerId) =>
      val authenticated = authenticatePlayer(principal)
      if (authenticated) {
        if (args.isEmpty) makeHome(playerId)
        else broadcast(principal, args.mkString(" "), recipients)
      }
      Command.success

    case _ =>
      if (args.isEmpty) logger.info(homeChannelPlayersOnly)
      else broadcast(principal, args.mkString(" "), recipients)
      Command.success
  }

  override def handle(event: Event): Unit = event match {
    case it: PlayerConnectEvent            => handle(it)
    case it: PlayerPermissionsChangedEvent => handle(it)
    case it: PlayerDisconnectEvent         => handle(it)
    case _                                 =>
  }

  private def handle(event: PlayerConnectEvent): Unit = {
    import event.playerId
    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(playerId)
        if (player.hasPermission(permission)) recipients += playerId
      case None => recipients += playerId
    }
  }

  private def handle(event: PlayerPermissionsChangedEvent): Unit = {
    import event.playerId
    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(playerId)
        if (recipients.contains(playerId)) {
          if (!player.hasPermission(permission)) recipients -= playerId
        } else if (player.hasPermission(permission)) recipients += playerId
      case None =>
    }
  }

  private def handle(event: PlayerDisconnectEvent): Unit = {
    import event.playerId
    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(playerId)
        if (player.hasPermission(permission)) recipients -= playerId
      case None => recipients -= playerId
    }
  }
}
