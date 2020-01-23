package gg.warcraft.chat.spigot

import gg.warcraft.chat.ChatService
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.profile.ChatProfileService
import org.bukkit.plugin.java.JavaPlugin

class ChatPlugin extends JavaPlugin {
  override def onLoad(): Unit = {
    saveDefaultConfig()
  }

  override def onEnable(): Unit = {
    val config = null // TODO

    ChannelService.read(config)

    EventService.subscribe(ChatProfileService)
    EventService.subscribe(ChannelService)
    EventService.subscribe(ChatService)

    getServer.getPluginManager.registerEvents(new SpigotChatEventMapper, this)
  }
}
