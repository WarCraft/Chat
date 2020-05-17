package gg.warcraft.chat.spigot

import gg.warcraft.chat.ChatConfig
import gg.warcraft.monolith.api.core.Codecs.Circe._
import gg.warcraft.monolith.api.core.ColorCode
import gg.warcraft.monolith.spigot.SpigotMonolithPlugin
import gg.warcraft.monolith.spigot.implicits._
import io.circe.generic.auto._
import io.circe.Decoder
import io.getquill.{SnakeCase, SqliteDialect}

class ChatPlugin extends SpigotMonolithPlugin {
  import implicits._

  override def onLoad(): Unit = {
    super.onLoad()

    implicit val databaseContext: DatabaseContext =
      initDatabase(SqliteDialect, SnakeCase, getDataFolder)
    upgradeDatabase(getDataFolder, getClassLoader)

    implicits.init()
  }

  override def onEnable(): Unit = {
    implicit val colorDecoder: Decoder[ColorCode] = enumDecoder(ColorCode.valueOf)

    // read config
    val config = parseConfig[ChatConfig](getConfig.saveToString)
    channelService.readConfig(config)
    profileService.readConfig(config)

    // subscribe handlers
    eventService.subscribe(profileService)
    eventService.subscribe(chatService)
    this.subscribe(chatEventMapper)
  }
}
