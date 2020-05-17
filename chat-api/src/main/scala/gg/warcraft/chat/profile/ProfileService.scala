package gg.warcraft.chat.profile

import java.util.UUID

import gg.warcraft.chat.ChatConfig
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.monolith.api.core.event.Event
import gg.warcraft.monolith.api.player.PlayerPreConnectEvent
import io.getquill.{SnakeCase, SqliteDialect}
import io.getquill.context.jdbc.JdbcContext

import scala.concurrent.{ExecutionContext, Future}
import scala.util.chaining._

class ProfileService(implicit
    database: JdbcContext[SqliteDialect, SnakeCase],
    channelService: ChannelService
) extends Event.Handler {
  import channelService.{channelsByName, defaultChannel}
  import database._

  private implicit val executionContext: ExecutionContext =
    ExecutionContext.global

  private var _defaultTag: String = _
  private var _profiles: Map[UUID, Profile] = Map.empty

  def defaultTag: String = _defaultTag
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
    Future { database.run { query[Profile].insert(lift(profile)) } }
    _profiles += (profile.playerId -> profile)
  }

  private def validateProfile(profile: Profile, playerName: String): Unit = {
    var validatedProfile = profile
    validatedProfile =
      if (profile.name == playerName) validatedProfile
      else validatedProfile.copy(name = playerName)
    validatedProfile =
      if (channelsByName.contains(profile.home)) validatedProfile
      else validatedProfile.copy(home = defaultChannel.name)
    if (validatedProfile != profile) saveProfile(validatedProfile)
  }

  override def handle(event: Event): Unit = event match {
    case it: PlayerPreConnectEvent => handlePlayerPreConnect(it)
    case _                         =>
  }

  private def handlePlayerPreConnect(event: PlayerPreConnectEvent): Unit = {
    import event.{name, playerId}
    _profiles.get(playerId).orElse(loadProfile(playerId)) match {
      case Some(profile) =>
        validateProfile(profile, name)
      case None =>
        Profile(playerId, name, defaultTag, defaultChannel.name)
          .pipe { saveProfile }
    }
  }
}
