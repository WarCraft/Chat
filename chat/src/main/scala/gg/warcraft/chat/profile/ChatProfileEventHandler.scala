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
    var dirty = false

    val semiValidProfile =
      if (profile.name != name) {
        dirty = true
        profile.copy(name = name)
      } else profile

    val validProfile =
      if (channelsByName.get(profile.home).isEmpty) {
        dirty = true
        semiValidProfile.copy(home = defaultChannel.name)
      } else semiValidProfile

    if (dirty) save(validProfile)
  }

  override def handle(event: Event): Unit = event match {
    case PlayerPreConnectEvent(playerId, name) =>
      profiles.get(playerId) match {
        case Some(profile) => update(profile, name)
        case None =>
          load(playerId) match {
            case Some(profile) => update(profile, name)
            case None =>
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
