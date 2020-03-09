package gg.warcraft.chat.akka

import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.chat.AsyncPlayerPreChatEvent
import gg.warcraft.monolith.api.core.command.{Command, CommandPreExecuteEvent}
import gg.warcraft.monolith.api.core.event.{EventHandler, EventService, PreEvent}

private[this] class ChatEventHandler(
    service: ActorRef[ChatService.Command]
) extends EventHandler {
  override def reduce[T <: PreEvent](event: T): T = {
    case event: AsyncPlayerPreChatEvent =>
      import event._
      service ! ChatService.RouteMessage(null, text) // TODO add player
      event.copy(cancelled = true).asInstanceOf[T]

    case event: CommandPreExecuteEvent =>
      import event._
      service ! ChatService.RouteMessage(null, args.mkString(" "), Some(label))
      channelService.channelsByAlias.get(label) match {
        case Some(channel) =>
          channel.handle(sender, Command(cmd, label, args))
          event.copy(cancelled = true).asInstanceOf[T]

        case None => event
      }

    case _ => event
  }
}

object ChatHandler {
  def apply(service: ActorRef[ChatService.Command])(
      implicit eventService: EventService
  ): Behavior[Unit] = {
    val eventHandler = new ChatEventHandler(service)
    eventService subscribe eventHandler

    Behaviors.receiveSignal {
      case (_, PostStop) =>
        eventService unsubscribe eventHandler
        Behaviors.stopped

      case _ => Behaviors.same
    }
  }
}
