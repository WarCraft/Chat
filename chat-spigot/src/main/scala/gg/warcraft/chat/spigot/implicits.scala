/*
 * MIT License
 *
 * Copyright (c) 2020 WarCraft <https://github.com/WarCraft>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.warcraft.chat.spigot

import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.{ProfileRepository, ProfileService}
import gg.warcraft.chat.{ChatConfig, ChatService}
import gg.warcraft.monolith.api.core.command.CommandService
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.core.task.TaskService
import gg.warcraft.monolith.spigot.implicits._
import org.bukkit.Server
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

import java.util.logging.Logger

object implicits {
  private var _config: ChatConfig = _

  private var _server: Server = _
  private var _plugin: Plugin = _
  private var _logger: Logger = _

  private var _commandService: CommandService = _
  private var _eventService: EventService = _
  private var _taskService: TaskService = _

  private[spigot] def init()(implicit
      server: Server,
      plugin: Plugin,
      logger: Logger,
      commandService: CommandService,
      eventService: EventService,
      taskService: TaskService
  ): Unit = {
    _server = server
    _plugin = plugin
    _logger = logger
    _commandService = commandService
    _eventService = eventService
    _taskService = taskService
  }

  private var _profileRepository: ProfileRepository = _

  private[spigot] def configure(
      config: ChatConfig,
      repositories: (
          ProfileRepository,
          Unit
      )
  ): Unit = {
    _config = config
    _profileRepository = repositories._1
  }

  // Spigot
  private implicit lazy val server: Server = _server
  private implicit lazy val plugin: Plugin = _plugin
  private implicit lazy val logger: Logger = _logger

  // Monolith
  private implicit lazy val commandService: CommandService = commandService
  private implicit lazy val eventService: EventService = _eventService
  private implicit lazy val taskService: TaskService = _taskService

  // Persistence
  private implicit lazy val profileRepository: ProfileRepository =
    _profileRepository

  // Chat
  implicit lazy val channelService: ChannelService = new ChannelService
  implicit lazy val profileService: ProfileService = new ProfileService
  implicit lazy val chatService: ChatService = new ChatService

  implicit lazy val messageAdapter: MessageAdapter = new SpigotMessageAdapter
  implicit lazy val chatEventMapper: Listener = new SpigotChatEventMapper
}
