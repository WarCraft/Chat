package gg.warcraft.chat.akka.profile

import akka.actor.typed.ActorRef
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.player.PlayerPreConnectEvent

class ProfileHandler(
    service: ActorRef[ProfileService.Command]
) extends EventHandler {
  override def handle(event: Event): Unit = event match {
    case PlayerPreConnectEvent(playerId, name) =>
      service ! ProfileService.ValidateProfile(playerId, name)

    case _ =>
  }
}
