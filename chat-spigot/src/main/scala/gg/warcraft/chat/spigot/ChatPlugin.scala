package gg.warcraft.chat.spigot

import java.util.logging.Logger

import gg.warcraft.chat.{ChatConfig, ChatService}
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.monolith.api.core.event.EventHandler
import gg.warcraft.monolith.spigot.Codecs.Circe._
import gg.warcraft.monolith.spigot.Implicits._
import io.circe.generic.auto._
import io.circe.yaml.parser
import org.bukkit.event.{HandlerList, Listener}
import org.bukkit.plugin.java.JavaPlugin

import scala.io.Source

class ChatPlugin extends JavaPlugin {
  private final val WARN_DEFAULT_CONFIG = "Falling back to default config!"

  private var eventHandlers: List[EventHandler] = Nil
  private var listeners: List[Listener] = Nil

  override def onLoad(): Unit = saveDefaultConfig()

  override def onEnable(): Unit = {
    implicit val logger: Logger = getLogger

    // initialize services
    implicit val channelService: ChannelService = new ChannelService
    implicit val profileService: ChatProfileService = new ChatProfileService
    implicit val chatService: ChatService = new ChatService

    implicit val messageAdapter: MessageAdapter = new SpigotMessageAdapter
    implicit val chatEventMapper: Listener = new SpigotChatEventMapper

    // read config
    def loadConfig(config: String)(
        implicit logger: Logger
    ): ChatConfig = {
      def onError(err: io.circe.Error): ChatConfig = {
        logger severe err.getMessage
        logger severe WARN_DEFAULT_CONFIG
        val defaultConfig = Source.fromResource("config.yml")
        loadConfig(defaultConfig.mkString)
      }

      parser.parse(config) match {
        case Left(err)   => onError(err)
        case Right(json) =>
          json.as[ChatConfig] match {
            case Left(err)     => onError(err)
            case Right(config) => config
          }
      }
    }

    val config = loadConfig(getConfig.saveToString)
    channelService readConfig config
    profileService readConfig config

    // subscribe handlers
    eventHandlers ::= profileService
    eventService subscribe profileService

    eventHandlers ::= chatService
    eventService subscribe chatService

    listeners ::= chatEventMapper
    getServer.getPluginManager.registerEvents(chatEventMapper, this)
  }

  override def onDisable(): Unit = {
    eventHandlers foreach eventService.unsubscribe
    eventHandlers = Nil

    listeners foreach HandlerList.unregisterAll
    listeners = Nil
  }
}
