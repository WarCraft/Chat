package gg.warcraft.chat.profile

import java.util.UUID

import com.typesafe.config.Config
import gg.warcraft.monolith.api.core.Repository

import scala.collection.mutable
import scala.concurrent.Future

object ChatProfileRepository {
  private val _profiles = mutable.Map[UUID, ChatProfile]()

  private var _defaultTag: String = _
}

class ChatProfileRepository(
    override protected implicit val dbConfig: Config
) extends Repository {
  import ChatProfileRepository._
  import db._

  def profiles: Map[UUID, ChatProfile] =
    _profiles.asInstanceOf[Map[UUID, ChatProfile]]

  def defaultTag: String = _defaultTag

  def load(playerId: UUID): Option[ChatProfile] =
    db.run(query[ChatProfile].filter(_.playerId == lift(playerId))).headOption

  def save(profile: ChatProfile): Option[ChatProfile] = {
    Future { db.run(query[ChatProfile].insert(lift(profile))) }
    _profiles.put(profile.playerId.get, profile)
  }
}
