package gg.warcraft.chat.akka.channel

import akka.actor.typed.ActorRef
import gg.warcraft.monolith.api.core.command.{Command, CommandHandler, CommandSender}
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.player.{
  PlayerConnectEvent, PlayerDisconnectEvent, PlayerPermissionsChangedEvent
}

class GlobalChannelHandler(
    channel: ActorRef[GlobalChannel.Command]
) extends CommandHandler
    with EventHandler {
  private final val WARN_CONSOLE_HOME_CHANNEL =
    "Only players can set their home channel!"

  override def handle(sender: CommandSender, cmd: Command): Boolean = sender match {
    case CommandSender(_, Some(playerId)) =>
      if (!cmd.args.isEmpty) {
        val message = cmd.args.mkString(" ")
        channel ! GlobalChannel.SendMessage(sender.ref, message)
      } else {} // TODO set player home channel
      true

    case CommandSender(_, None) =>
      if (!cmd.args.isEmpty) {
        val message = cmd.args.mkString(" ")
        channel ! GlobalChannel.BroadcastMessage(message)
      } else sender.ref ! MessageAble.SendMessage(WARN_CONSOLE_HOME_CHANNEL)
      true
  }

  override def handle(event: Event): Unit = {
    case event: PlayerConnectEvent =>
      channel ! GlobalChannel.AddPlayer(event.ref)

    case event: PlayerDisconnectEvent =>
      channel ! GlobalChannel.RemovePlayer(event.ref)

    case event: PlayerPermissionsChangedEvent =>
      channel ! GlobalChannel.RemovePlayer(event.ref)
      channel ! GlobalChannel.AddPlayer(event.ref)

    case _ =>
  }
}
