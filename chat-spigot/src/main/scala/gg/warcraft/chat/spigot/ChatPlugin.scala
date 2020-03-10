package gg.warcraft.chat.spigot

import gg.warcraft.chat.{ChatConfig, ChatService}
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.monolith.spigot.Codecs.Circe._
import gg.warcraft.monolith.spigot.Implicits._
import gg.warcraft.monolith.spigot.SpigotMonolithPlugin
import io.circe.generic.auto._
import io.getquill.context.jdbc.JdbcContext
import org.bukkit.event.Listener

class ChatPlugin extends SpigotMonolithPlugin {
  override def onLoad(): Unit = saveDefaultConfig()

  override def onEnable(): Unit = {
    implicit val database: JdbcContext[_, _] = initDatabase(getDataFolder)

    // initialize services
    implicit val channelService: ChannelService = new ChannelService
    implicit val profileService: ChatProfileService = new ChatProfileService
    implicit val chatService: ChatService = new ChatService

    implicit val messageAdapter: MessageAdapter = new SpigotMessageAdapter
    implicit val chatEventMapper: Listener = new SpigotChatEventMapper

    // read config
    val config = parseConfig[ChatConfig](getConfig.saveToString)
    channelService readConfig config
    profileService readConfig config

    // subscribe handlers
    eventService subscribe profileService
    eventService subscribe chatService
    this subscribe chatEventMapper
  }
}
