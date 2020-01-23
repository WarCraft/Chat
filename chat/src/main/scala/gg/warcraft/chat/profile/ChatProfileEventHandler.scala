package gg.warcraft.chat.profile

import gg.warcraft.chat.channel.ChannelRepository
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.player.PlayerPreConnectEvent

class ChatProfileEventHandler(
    private implicit val channelRepo: ChannelRepository,
    private implicit val profileRepo: ChatProfileRepository
) extends EventHandler {
  import channelRepo.{channelsByName, defaultChannel}
  import profileRepo.{profiles, save}

  override def handle(event: Event): Unit = event match {
    case PlayerPreConnectEvent(playerId, name) =>
      profiles.get(playerId) match {
        case Some(profile) =>
          channelsByName.get(profile.home) match {
            case None => save(profile.copy(home = defaultChannel.name))
            case _    => ()
          }
        case None =>
          save(ChatProfile(name, Some(playerId), "Wayfarer", defaultChannel.name))
      }

    case _ => ()
  }
}
