package gg.warcraft.chat.channel

import gg.warcraft.chat.ChatConfig
import gg.warcraft.monolith.api.core.event.EventService

class ChannelService(implicit eventService: EventService) {
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

  def readConfig(config: ChatConfig): Unit = {
    var channels: List[Channel] = List.empty
    var channelsByName: Map[String, Channel] = Map.empty
    var channelsByAlias: Map[String, Channel] = Map.empty
    var channelsByShortcut: Map[String, Channel] = Map.empty

    (config.globalChannels ++ config.localChannels).foreach { channel =>
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
