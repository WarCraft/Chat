package gg.warcraft.chat.spigot

import java.util.logging.Logger

import gg.warcraft.chat.ChatService
import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.core.task.TaskService
import gg.warcraft.monolith.spigot.implicits._
import io.getquill.{SnakeCase, SqliteDialect}
import io.getquill.context.jdbc.JdbcContext
import org.bukkit.Server
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

object implicits {
  private[spigot] type DatabaseContext = JdbcContext[SqliteDialect, SnakeCase]

  private var _server: Server = _
  private var _plugin: Plugin = _
  private var _logger: Logger = _

  private var _database: DatabaseContext = _
  private var _eventService: EventService = _
  private var _taskService: TaskService = _

  private[spigot] def init()(implicit
      server: Server,
      plugin: Plugin,
      logger: Logger,
      database: DatabaseContext,
      eventService: EventService,
      taskService: TaskService
  ): Unit = {
    _server = server
    _plugin = plugin
    _logger = logger
    _database = database
    _eventService = eventService
    _taskService = taskService
  }

  // Spigot
  private implicit lazy val server: Server = _server
  private implicit lazy val plugin: Plugin = _plugin
  private implicit lazy val logger: Logger = _logger

  // Core
  private implicit lazy val database: DatabaseContext = _database
  private implicit lazy val eventService: EventService = _eventService
  private implicit lazy val taskService: TaskService = _taskService

  // Chat
  implicit val channelService: ChannelService = new ChannelService
  implicit val profileService: ProfileService = new ProfileService
  implicit val chatService: ChatService = new ChatService

  implicit val messageAdapter: MessageAdapter = new SpigotMessageAdapter
  implicit val chatEventMapper: Listener = new SpigotChatEventMapper
}
