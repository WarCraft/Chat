package gg.warcraft.chat.profile

import java.util.UUID

import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.player.PlayerConnectEvent

import scala.collection.mutable

object ChatProfileService extends EventHandler {
  private val _profiles = mutable.Map[UUID, ChatProfile]()

  def profiles: Map[UUID, ChatProfile] =
    _profiles.asInstanceOf[Map[UUID, ChatProfile]]

  def save(profile: ChatProfile): Unit = profile match {
    case ChatProfile(_, Some(playerId), _, _, _) =>
      _profiles.put(playerId, profile) match {
        case Some(prev) =>
          if (profile.homeChannel != prev.homeChannel) {
            // TODO fire home channel changed event
          }
        case None => ()
      }

    case _ => throw new IllegalArgumentException
  }

  override def handle(event: Event): Unit = event match {
    case PlayerConnectEvent(playerId, name) =>
      _profiles.get(playerId) match {
        case Some(profile) =>
         ChannelService.channelsByName.get(profile.homeChannel) match {
           case None =>
             // TODO update profile with new default channel
           case _ => ()
         }
        case None =>
          val homeChannel = ChannelService.defaultChannel
          val profile = ChatProfile(
            name,
            Some(playerId),
            None,
            homeChannel.name,
            Set.empty
          )
          _profiles += (playerId -> profile)
      }
//      ChatProfile profile = profileQueryService.getChatProfile(playerId);
//      if (profile == null) {
//        Player player = playerQueryService.getPlayer(playerId);
//        Channel defaultChannel = channelQueryService.getDefaultChannel();
//        profileCommandService.createChatProfile(playerId, player.getName(), defaultChannel);
//      } else {
//        Channel homeChannel = channelQueryService.getChannelByAlias(profile.getHomeChannel());
//        if (homeChannel == null) {
//          Channel defaultChannel = channelQueryService.getDefaultChannel();
//          profileCommandService.setHomeChannel(playerId, defaultChannel);
//        }
//      }
  }
}
