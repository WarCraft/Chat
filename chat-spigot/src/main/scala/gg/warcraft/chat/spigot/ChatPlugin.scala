package gg.warcraft.chat.spigot

import gg.warcraft.chat.{ ChatConfig, ChatService }
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.spigot.Codecs.Circe._
import gg.warcraft.monolith.spigot.SpigotMonolithPlugin
import io.circe.generic.auto._
import io.getquill.context.jdbc.JdbcContext
import io.getquill.{ SnakeCase, SqliteDialect }
import org.bukkit.event.Listener

class ChatPlugin extends SpigotMonolithPlugin {
  override def onEnable(): Unit = {
    implicit val database: JdbcContext[_, _] =
      initDatabase(SqliteDialect, SnakeCase, getDataFolder)

    // initialize services
    implicit val channelService: ChannelService = new ChannelService
    implicit val profileService: ProfileService = new ProfileService
    implicit val chatService: ChatService = new ChatService

    implicit val messageAdapter: MessageAdapter = new SpigotMessageAdapter
    implicit val chatEventMapper: Listener = new SpigotChatEventMapper

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
