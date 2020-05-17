package gg.warcraft.chat.profile

import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.monolith.api.core.event.{Event, PreEvent}
import gg.warcraft.monolith.api.player.{PlayerDisconnectEvent, PlayerPreConnectEvent}

class ProfileCacheHandler(implicit
    profileService: ProfileService,
    channelService: ChannelService
) extends Event.Handler {
  import profileService._

  override def handle(event: Event): Unit = event match {
    case PlayerDisconnectEvent(player) => invalidateProfile(player.id)
    case _                             =>
  }

  override def reduce[T <: PreEvent](event: T): T = event match {
    case PlayerPreConnectEvent(playerId, name, _, _) =>
      loadProfile(playerId) match {
        case Some(profile) => validateProfile(profile, name)
        case None          => createProfile(playerId, name)
      }
      event

    case _ => event
  }
}
