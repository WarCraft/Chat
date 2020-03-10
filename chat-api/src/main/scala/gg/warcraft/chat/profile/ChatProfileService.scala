package gg.warcraft.chat.profile

import java.util.UUID

import gg.warcraft.chat.ChatConfig
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.player.PlayerPreConnectEvent
import io.getquill.context.jdbc.JdbcContext

import scala.concurrent.{ExecutionContext, Future}

object ChatProfileService {
  private var _defaultTag: String = _
  private var _profiles = Map[UUID, ChatProfile]()
}

class ChatProfileService(
    private implicit val database: JdbcContext[_, _],
    private implicit val context: ExecutionContext,
    private implicit val channelService: ChannelService
) extends EventHandler {
  import ChatProfileService._
  import channelService.{channelsByName, defaultChannel}
  import database._

  def defaultTag: String = _defaultTag
  def profiles: Map[UUID, ChatProfile] = _profiles

  def readConfig(config: ChatConfig): Unit =
    _defaultTag = config.defaultTag

  def loadProfile(playerId: UUID): Option[ChatProfile] = {
    database
      .run(query[ChatProfile] filter (_.playerId == lift(playerId)))
      .headOption match {
      case Some(profile) => _profiles += (playerId -> profile); Some(profile)
      case None          => None
    }
  }

  def saveProfile(profile: ChatProfile): Unit = {
    Future { database.run(query[ChatProfile] insert lift(profile)) }
    _profiles += (profile.playerId -> profile)
  }

  override def handle(event: Event): Unit = event match {
    case it: PlayerPreConnectEvent => handle(it)
    case _                         => ()
  }

  private def handle(event: PlayerPreConnectEvent): Unit = {
    import event.{name, playerId}

    _profiles.get(playerId).orElse(loadProfile(playerId)) match {
      case Some(profile) =>
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

        if (dirty) saveProfile(validProfile)

      case None =>
        val newProfile = ChatProfile(
          name,
          playerId,
          defaultTag,
          defaultChannel.name
        )
        saveProfile(newProfile)
    }
  }
}
