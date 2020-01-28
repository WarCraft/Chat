package gg.warcraft.chat.profile

import java.util.UUID

import com.typesafe.config.Config
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.monolith.api.core.Repository
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.player.PlayerPreConnectEvent

import scala.collection.mutable
import scala.concurrent.Future

object ChatProfileService {
  private val _profiles = mutable.Map[UUID, ChatProfile]()

  private var _defaultTag: String = _
}

class ChatProfileService(
    private implicit val channelService: ChannelService,
    override protected implicit val dbConfig: Config
) extends EventHandler
    with Repository {
  import ChatProfileService._
  import channelService.{channelsByName, defaultChannel}
  import db._

  def profiles: Map[UUID, ChatProfile] =
    _profiles.asInstanceOf[Map[UUID, ChatProfile]]

  def defaultTag: String = _defaultTag

  def loadProfile(playerId: UUID): Option[ChatProfile] = {
    val profile = db
      .run(query[ChatProfile].filter(_.playerId == lift(playerId)))
      .headOption
    if (profile.isDefined) _profiles += (playerId -> profile.get)
    profile
  }

  def saveProfile(profile: ChatProfile): Option[ChatProfile] = {
    Future { db.run(query[ChatProfile].insert(lift(profile))) }
    _profiles.put(profile.playerId, profile)
  }

  private def updateProfile(profile: ChatProfile, name: String): Unit = {
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
  }

  override def handle(event: Event): Unit = event match {
    case it: PlayerPreConnectEvent => handle(it)
    case _                         => ()
  }

  private def handle(event: PlayerPreConnectEvent): Unit = {
    import event.{name, playerId}

    profiles.get(playerId) match {
      case Some(profile) => updateProfile(profile, name)
      case None =>
        loadProfile(playerId) match {
          case Some(profile) => updateProfile(profile, name)
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
}
