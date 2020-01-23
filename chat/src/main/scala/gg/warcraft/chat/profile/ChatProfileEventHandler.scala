package gg.warcraft.chat.profile

import gg.warcraft.chat.channel.ChannelRepository
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.player.PlayerPreConnectEvent

class ChatProfileEventHandler(
    private implicit val channelRepo: ChannelRepository,
    private implicit val profileRepo: ChatProfileRepository
) extends EventHandler {
  import channelRepo.{channelsByName, defaultChannel}
  import profileRepo.{defaultTag, load, profiles, save}

  private def update(profile: ChatProfile, name: String): Unit = {
    var update = false

    val nameProfile =
      if (profile.name != name) {
        update = true
        profile.copy(name = name)
      } else profile

    val nameChannelProfile =
      if (channelsByName.get(profile.home).isDefined) nameProfile
      else {
        update = true
        nameProfile.copy(home = defaultChannel.name)
      }

    if (update) save(nameChannelProfile)
  }

  override def handle(event: Event): Unit = event match {
    case PlayerPreConnectEvent(playerId, name) =>
      profiles.get(playerId) match {
        case Some(it) => update(it, name)
        case None =>
          load(playerId) match {
            case Some(it) => update(it, name)
            case _ =>
              val newProfile = ChatProfile(
                name,
                playerId,
                defaultTag,
                defaultChannel.name
              )
              save(newProfile)
          }
      }

    case _ => ()
  }
}
