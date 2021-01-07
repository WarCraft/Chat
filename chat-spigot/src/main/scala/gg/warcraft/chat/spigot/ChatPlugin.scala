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

package gg.warcraft.chat.spigot

import gg.warcraft.chat.ChatConfig
import gg.warcraft.chat.profile.{
  PostgresProfileRepository, ProfileCacheHandler, ProfileRepository,
  SqliteProfileRepository
}
import gg.warcraft.monolith.api.core.DatabaseConfig
import gg.warcraft.monolith.spigot.SpigotMonolithPlugin
import io.circe.generic.auto._

class ChatPlugin extends SpigotMonolithPlugin {
  import gg.warcraft.monolith.spigot.implicits._
  import implicits._

  override def onLoad(): Unit = {
    super.onLoad()
    implicits.init()
  }

  override def onEnable(): Unit = {
    import gg.warcraft.monolith.api.util.codecs.circe._
    import gg.warcraft.monolith.api.util.codecs.monolith._
    val config = parseConfig[ChatConfig](getConfig.saveToString)

    upgradeDatabase(config.database, getDataFolder, getClassLoader)
    val repositories = configureRepositories(config.database)
    implicits.configure(config, repositories)

    channelService.readConfig(config)
    profileService.readConfig(config)

    eventService.subscribe(chatService)
    eventService.subscribe(new ProfileCacheHandler)
    this.subscribe(chatEventMapper)
  }

  private def configureRepositories(config: DatabaseConfig): (
      ProfileRepository,
      Unit
  ) =
    if (config.embedded) {
      val sqliteConfig = parseDatabaseConfig(config, getDataFolder)
      (
        new SqliteProfileRepository(sqliteConfig),
        ()
      )
    } else {
      val postgresConfig = parseDatabaseConfig(config, getDataFolder)
      (
        new PostgresProfileRepository(postgresConfig),
        ()
      )
    }
}
