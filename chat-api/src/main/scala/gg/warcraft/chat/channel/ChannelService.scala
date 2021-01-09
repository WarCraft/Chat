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

package gg.warcraft.chat.channel

import gg.warcraft.chat.ChatConfig
import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.entity.EntityService
import gg.warcraft.monolith.api.player.PlayerService

import java.util.logging.Logger

class ChannelService(implicit
    eventService: EventService,
    logger: Logger,
    entityService: EntityService,
    playerService: PlayerService,
    messageAdapter: MessageAdapter
) {
  private var _channels: List[Channel] = Nil
  private var _channelsByName: Map[String, Channel] = Map.empty
  private var _channelsByAlias: Map[String, Channel] = Map.empty
  private var _channelsByShortcut: Map[String, Channel] = Map.empty
  private var _defaultChannel: Channel = _

  def channels: List[Channel] = _channels
  def channelsByName: Map[String, Channel] = _channelsByName
  def channelsByAlias: Map[String, Channel] = _channelsByAlias
  def channelsByShortcut: Map[String, Channel] = _channelsByShortcut
  def defaultChannel: Channel = _defaultChannel

  def readConfig(
      config: ChatConfig
  )(implicit
      profileService: ProfileService
  ): Unit = {
    var channels: List[Channel] = List.empty
    var channelsByName: Map[String, Channel] = Map.empty
    var channelsByAlias: Map[String, Channel] = Map.empty
    var channelsByShortcut: Map[String, Channel] = Map.empty

    val globalChannels = config.globalChannels.map { config =>
      import config._
      GlobalChannel(name, aliases, shortcut, color, format, permission)
    }

    val localChannels = config.localChannels.map { config =>
      import config._
      LocalChannel(name, aliases, shortcut, color, format, radius)
    }

    (globalChannels ++ localChannels).foreach { channel =>
      channels ::= channel
      channelsByName += (channel.name -> channel)
      channel.aliases.foreach { it =>
        channelsByAlias += (it.toLowerCase -> channel)
      }
      channel.shortcut.foreach { it =>
        channelsByShortcut += (it.toLowerCase -> channel)
      }
    }

    // only after successfully setting the default channel using a
    // volatile get do we overwrite the active channel collections
    _defaultChannel = channelsByName(config.defaultChannel)

    _channels.foreach {
      case it: GlobalChannel => eventService.unsubscribe(it)
      case _                 =>
    }

    _channels = channels
    _channelsByName = channelsByName
    _channelsByAlias = channelsByAlias
    _channelsByShortcut = channelsByShortcut

    _channels.foreach {
      case it: GlobalChannel => eventService.subscribe(it)
      case _                 =>
    }
  }
}
