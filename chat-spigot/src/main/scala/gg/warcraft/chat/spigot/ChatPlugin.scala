package gg.warcraft.chat.spigot

import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.chat.{ChatConfig, ChatService}
import gg.warcraft.monolith.spigot.Implicits._
import io.circe.parser._
import org.bukkit.plugin.java.JavaPlugin

class ChatPlugin extends JavaPlugin {
  implicit val playerQueryService = null // TODO add to monolith implicits

  override def onLoad(): Unit = saveDefaultConfig()

  override def onEnable(): Unit = {
    implicit val messageAdapter: MessageAdapter = new SpigotMessageAdapter
    implicit val channelService: ChannelService = new ChannelService
    implicit val profileService: ChatProfileService = new ChatProfileService
    implicit val chatService: ChatService = new ChatService

    decode[ChatConfig](getConfig.saveToString()) match {
      case Left(err) =>
        getLogger.severe("Failed to parse configuration: " + err.getMessage)
      case Right(config) =>
        channelService.readConfig(config)
        profileService.readConfig(config)
    }

    eventService.subscribe(profileService)
    eventService.subscribe(chatService)

    getServer.getPluginManager.registerEvents(new SpigotChatEventMapper, this)
  }
}
