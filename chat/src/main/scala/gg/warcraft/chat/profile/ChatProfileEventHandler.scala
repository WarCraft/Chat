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

  private def updateHome(profile: ChatProfile): Unit =
    channelsByName
      .get(profile.home)
      .orElse(save(profile.copy(home = defaultChannel.name)))

  override def handle(event: Event): Unit = event match {
    case PlayerPreConnectEvent(playerId, name) =>
      profiles.get(playerId) match {
        case Some(it) => updateHome(it)
        case None =>
          load(playerId) match {
            case Some(it) => updateHome(it)
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
