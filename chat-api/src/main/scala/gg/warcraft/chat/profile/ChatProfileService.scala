package gg.warcraft.chat.profile

import java.util.UUID

import gg.warcraft.monolith.api.util.ColorCode

import scala.collection.mutable

object ChatProfileService {
  private val profiles = mutable.Map[UUID, ChatProfile]()
}

class ChatProfileService {
  import ChatProfileService.profiles

  val consoleProfile: ChatProfile =
    ChatProfile(None, "Console", ChatTag("Server", ColorCode.YELLOW), None, Set())

  def getProfile(playerId: UUID): Option[ChatProfile] =
    profiles.get(playerId)

  def saveProfile(playerId: UUID, profile: ChatProfile): Unit = {
    profiles.put(playerId, profile) match {
      case Some(prev) => if (profile.homeChannel != prev.homeChannel) {
        // TODO fire home channel changed event
      }
      case None       => ()
    }
  }

  /*
    public void onPlayerConnect(PlayerConnectEvent event) {
      UUID playerId = event.playerId();
      ChatProfile profile = profileQueryService.getChatProfile(playerId);
      if (profile == null) {
          Player player = playerQueryService.getPlayer(playerId);
          Channel defaultChannel = channelQueryService.getDefaultChannel();
          profileCommandService.createChatProfile(playerId, player.getName(), defaultChannel);
      } else {
          Channel homeChannel = channelQueryService.getChannelByAlias(profile.getHomeChannel());
          if (homeChannel == null) {
              Channel defaultChannel = channelQueryService.getDefaultChannel();
              profileCommandService.setHomeChannel(playerId, defaultChannel);
          }
      }
    }
   */
}
