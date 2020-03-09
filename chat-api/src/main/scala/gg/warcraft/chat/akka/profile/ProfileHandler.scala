package gg.warcraft.chat.akka.profile

import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.monolith.api.core.event.{Event, EventHandler, EventService}
import gg.warcraft.monolith.api.player.PlayerPreConnectEvent

private[this] class ProfileEventHandler(
    service: ActorRef[ProfileService.Command]
) extends EventHandler {
  override def handle(event: Event): Unit = event match {
    case PlayerPreConnectEvent(playerId, name) =>
      service ! ProfileService.ValidateProfile(playerId, name)

    case _ =>
  }
}

object ProfileHandler {
  def apply(service: ActorRef[ProfileService.Command])(
      implicit eventService: EventService
  ): Behavior[Unit] = {
    val eventHandler = new ProfileEventHandler(service)
    eventService subscribe eventHandler

    Behaviors.receiveSignal {
      case (_, PostStop) =>
        eventService unsubscribe eventHandler
        Behaviors.stopped

      case _ => Behaviors.same
    }
  }
}
