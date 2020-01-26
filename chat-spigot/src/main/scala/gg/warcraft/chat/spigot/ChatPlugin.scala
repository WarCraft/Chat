package gg.warcraft.chat.spigot

import com.typesafe.config.Config
import gg.warcraft.chat.channel.{ChannelEventHandler, ChannelRepository}
import gg.warcraft.chat.profile.{ChatProfileEventHandler, ChatProfileRepository}
import gg.warcraft.chat.{ChatConfig, ChatService}
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.core.{AuthorizationService, TaskService}
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin

class ChatPlugin extends JavaPlugin {
  override def onLoad(): Unit = {
    saveDefaultConfig()
  }

  override def onEnable(): Unit = {
    val config: ChatConfig = null // TODO map getConfig.toString
    init(config)
  }

  private def init(
      config: ChatConfig
  )(
      implicit dbConfig: Config,
      server: Server,
      authService: AuthorizationService,
      eventService: EventService,
      taskService: TaskService
  ): Unit = {
    implicit val messageAdapter = new SpigotMessageAdapter

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

    eventService.subscribe(channelHandler)
    eventService.subscribe(profileHandler)
    eventService.subscribe(chatService)

    getServer.getPluginManager.registerEvents(new SpigotChatEventMapper, this)
  }
}
