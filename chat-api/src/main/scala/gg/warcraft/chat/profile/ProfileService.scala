package gg.warcraft.chat.profile

import java.util.UUID

import gg.warcraft.chat.ChatConfig
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.monolith.api.core.event.Event
import gg.warcraft.monolith.api.player.PlayerPreConnectEvent
import gg.warcraft.monolith.api.util.Ops._
import io.getquill.context.jdbc.JdbcContext

import scala.concurrent.{ExecutionContext, Future}
import scala.util.chaining._

class ProfileService(
    private implicit val database: JdbcContext[_, _],
    private implicit val context: ExecutionContext,
    private implicit val channelService: ChannelService
) extends Event.Handler {
  import channelService.{channelsByName, defaultChannel}
  import database._

  private var _defaultTag: String = _
  def defaultTag: String = _defaultTag

  private var _profiles: Map[UUID, Profile] = Map.empty
  def profiles: Map[UUID, Profile] = _profiles

  def readConfig(config: ChatConfig): Unit =
    _defaultTag = config.defaultTag

  def loadProfile(playerId: UUID): Option[Profile] = database
    .run { query[Profile].filter { _.playerId == lift(playerId) } }
    .headOption
    .tap {
      case Some(profile) => _profiles += (playerId -> profile)
      case None          =>
    }

  def saveProfile(profile: Profile): Unit = {
    Future { database.run(query[Profile] insert lift(profile)) }
    _profiles += (profile.playerId -> profile)
  }

  private def validateProfile(profile: Profile, playerName: String): Unit = {
    var dirty = false

    val semiValidProfile =
      if (profile.name != playerName) {
        dirty = true
        profile.copy(name = playerName)
      } else profile

    val validProfile =
      if (!channelsByName.contains(profile.home)) {
        dirty = true
        semiValidProfile.copy(home = defaultChannel.name)
      } else semiValidProfile

    if (dirty) saveProfile(validProfile)
  }

  override def handle(event: Event): Unit = event match {
    case it: PlayerPreConnectEvent => handle(it)
    case _                         =>
  }

  private def handle(event: PlayerPreConnectEvent): Unit = {
    import event.{name, playerId}
    _profiles.get(playerId).orElse(loadProfile(playerId)) match {
      case Some(profile) =>
        validateProfile(profile, name)
      case None =>
        saveProfile <| Profile(playerId, name, defaultTag, defaultChannel.name)
    }
  }
}
