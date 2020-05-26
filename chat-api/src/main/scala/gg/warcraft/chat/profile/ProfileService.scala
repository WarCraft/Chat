/*
 * MIT License
 *
 * Copyright (c) 2020 WarCraft <https://github.com/WarCraft>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.warcraft.chat.profile

import java.util.UUID

import gg.warcraft.chat.ChatConfig
import gg.warcraft.chat.channel.ChannelService
import io.getquill.{SnakeCase, SqliteDialect}
import io.getquill.context.jdbc.JdbcContext

import scala.concurrent.{ExecutionContext, Future}
import scala.util.chaining._

class ProfileService(implicit
    database: JdbcContext[SqliteDialect, SnakeCase],
    channelService: ChannelService
) {
  import channelService.{channelsByName, defaultChannel}
  import database._

  private implicit val executionContext: ExecutionContext =
    ExecutionContext.global

  private var _defaultTag: String = _
  private var _profiles: Map[UUID, Profile] = Map.empty

  def defaultTag: String = _defaultTag
  def profiles: Map[UUID, Profile] = _profiles

  private[chat] def readConfig(config: ChatConfig): Unit =
    _defaultTag = config.defaultTag

  private[profile] def loadProfile(playerId: UUID): Option[Profile] = database
    .run { query[Profile].filter { _.playerId == lift(playerId) } }
    .headOption
    .tap {
      case Some(profile) => _profiles += (playerId -> profile)
      case None          =>
    }

  private[profile] def validateProfile(
      profile: Profile,
      playerName: String
  ): Unit = {
    var validatedProfile = profile
    validatedProfile =
      if (profile.name == playerName) validatedProfile
      else validatedProfile.copy(name = playerName)
    validatedProfile =
      if (channelsByName.contains(profile.home)) validatedProfile
      else validatedProfile.copy(home = defaultChannel.name)
    if (validatedProfile != profile) saveProfile(validatedProfile)
  }

  private[profile] def createProfile(playerId: UUID, name: String): Unit = {
    val profile = Profile(playerId, name, defaultTag, defaultChannel.name)
    _profiles += (profile.playerId -> profile)
    database.run { query[Profile].insert(lift(profile)) }
  }

  private[profile] def invalidateProfile(playerId: UUID): Unit =
    _profiles -= playerId

  def saveProfile(profile: Profile): Unit = {
    _profiles += (profile.playerId -> profile)
    Future {
      database.run {
        query[Profile]
          .insert { lift(profile) }
          .onConflictUpdate(_.playerId)(
            (_1, _2) => _1.name -> _2.name,
            (_1, _2) => _1.tag -> _2.tag,
            (_1, _2) => _1.home -> _2.home
          )
      }
    }
  }
}
