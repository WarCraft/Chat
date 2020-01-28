package gg.warcraft.chat.spigot

import com.typesafe.config.Config
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.chat.{ChatConfig, ChatService}
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.core.{AuthorizationService, TaskService}
import gg.warcraft.monolith.spigot.Implicits._
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin

class ChatPlugin extends JavaPlugin {
  override def onLoad(): Unit = saveDefaultConfig()

  override def onEnable(): Unit = {
    val config = yamlMapper.readValue(getConfig.toString, classOf[ChatConfig])
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
    implicit val channelService = new ChannelService
    implicit val profileService = new ChatProfileService
    implicit val chatService = new ChatService

    channelService.readConfig(config)
    profileService.readConfig(config)

    eventService.subscribe(profileService)
    eventService.subscribe(chatService)

    getServer.getPluginManager.registerEvents(new SpigotChatEventMapper, this)
  }
}
