package gg.warcraft.chat.akka.channel

import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.monolith.api.core.command.{
  Command, CommandHandler, CommandSender, CommandService
}
import gg.warcraft.monolith.api.core.event.{Event, EventHandler, EventService}
import gg.warcraft.monolith.api.player.{
  PlayerConnectEvent, PlayerDisconnectEvent, PlayerPermissionsChangedEvent
}

private[this] class GlobalChannelCommandHandler(
    channel: ActorRef[GlobalChannel.Command]
) extends CommandHandler {
  private final val WARN_CONSOLE_HOME_CHANNEL =
    "Only players can set their home channel!"

  override def handle(sender: CommandSender, cmd: Command): Boolean = {
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
}

private[this] class GlobalChannelEventHandler(
    channel: ActorRef[GlobalChannel.Command]
) extends EventHandler {
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

object GlobalChannelHandler {
  def apply(channel: ActorRef[GlobalChannel.Command])(
      implicit commandService: CommandService,
      eventService: EventService
  ): Behavior[Unit] = {
    val commandHandler = new GlobalChannelCommandHandler(channel)
    commandService subscribe commandHandler

    val eventHandler = new GlobalChannelEventHandler(channel)
    eventService subscribe eventHandler

    Behaviors.receiveSignal {
      case (_, PostStop) =>
        commandService unsubscribe commandHandler
        eventService unsubscribe eventHandler
        Behaviors.stopped

      case _ => Behaviors.same
    }
  }
}
