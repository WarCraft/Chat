package gg.warcraft.chat.spigot

import gg.warcraft.chat.channel.{ChannelEventHandler, ChannelRepository}
import gg.warcraft.chat.profile.{ChatProfileEventHandler, ChatProfileRepository}
import gg.warcraft.chat.{ChatConfig, ChatService}
import org.bukkit.plugin.java.JavaPlugin

class ChatPlugin extends JavaPlugin {
  override def onLoad(): Unit = {
    saveDefaultConfig()
  }

  override def onEnable(): Unit = {
    val config: ChatConfig = null // TODO

    implicit val channelRepo = new ChannelRepository
    implicit val channelHandler = new ChannelEventHandler

    implicit val profileRepo = new ChatProfileRepository
    implicit val profileHandler = new ChatProfileEventHandler

    implicit val chatService = new ChatService

    config.globalChannels.foreach(it => {
      channelRepo.save(it, it.name == config.defaultChannel)
    })
    config.localChannels.foreach(it => {
      channelRepo.save(it, it.name == config.defaultChannel)
    })

    EventService.subscribe(channelHandler)
    EventService.subscribe(profileHandler)
    EventService.subscribe(chatService)

    getServer.getPluginManager.registerEvents(new SpigotChatEventMapper, this)
  }
}
