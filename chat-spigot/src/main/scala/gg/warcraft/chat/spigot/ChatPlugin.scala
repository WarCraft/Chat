package gg.warcraft.chat.spigot

import com.typesafe.config.Config
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.chat.{ChatConfig, ChatService}
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.core.{AuthorizationService, TaskService}
import gg.warcraft.monolith.spigot.Implicits._
import org.bukkit.Server
import org.bukkit.plugin.java.JavaPlugin

class ChatPlugin extends JavaPlugin {
  override def onLoad(): Unit = saveDefaultConfig()

  override def onEnable(): Unit = init(
    yamlMapper.readValue(getConfig.toString, classOf[ChatConfig])
  )

  private def init(
      config: ChatConfig
  )(
      implicit dbConfig: Config,
      server: Server,
      authService: AuthorizationService,
      eventService: EventService,
      taskService: TaskService
  ): Unit = {
    implicit val messageAdapter: MessageAdapter = new SpigotMessageAdapter
    implicit val channelService: ChannelService = new ChannelService
    implicit val profileService: ChatProfileService = new ChatProfileService
    implicit val chatService: ChatService = new ChatService

    channelService.readConfig(config)
    profileService.readConfig(config)

    eventService.subscribe(profileService)
    eventService.subscribe(chatService)

    getServer.getPluginManager.registerEvents(new SpigotChatEventMapper, this)
  }
}
