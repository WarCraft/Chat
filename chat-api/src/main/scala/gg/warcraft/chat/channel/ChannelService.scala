package gg.warcraft.chat.channel

import gg.warcraft.chat.ChatConfig
import gg.warcraft.monolith.api.core.event.EventService

import scala.collection.mutable

object ChannelService {
  private var _channels = List[Channel]()
  private var _channelsByName = Map[String, Channel]()
  private var _channelsByAlias = Map[String, Channel]()
  private var _channelsByShortcut = Map[String, Channel]()
  private var _defaultChannel: Channel = _
}

class ChannelService(implicit eventService: EventService) {
  import ChannelService._

  def channels: List[Channel] = _channels
  def channelsByName: Map[String, Channel] = _channelsByName
  def channelsByAlias: Map[String, Channel] = _channelsByAlias
  def channelsByShortcut: Map[String, Channel] = _channelsByShortcut
  def defaultChannel: Channel = _defaultChannel

  def readConfig(config: ChatConfig): Unit = {
    val channels = mutable.ListBuffer[Channel]()
    val channelsByName = mutable.Map[String, Channel]()
    val channelsByAlias = mutable.Map[String, Channel]()
    val channelsByShortcut = mutable.Map[String, Channel]()

    (config.globalChannels ++ config.localChannels) foreach { channel =>
      channels += channel
      channelsByName += (channel.name -> channel)
      channel.aliases foreach (it => channelsByAlias += (it.toLowerCase -> channel))
      channel.shortcut map (it => channelsByShortcut += (it.toLowerCase -> channel))
    }

    // only after successfully setting the default channel using the
    // volatile accessor do we overwrite the active channel collections
    _defaultChannel = _channelsByName(config.defaultChannel)

    _channels foreach {
      case it: GlobalChannel => eventService unsubscribe it
      case _                 =>
    }

    _channels = channels.toList
    _channelsByName = channelsByName.toMap
    _channelsByAlias = channelsByAlias.toMap
    _channelsByShortcut = channelsByShortcut.toMap

    _channels foreach {
      case it: GlobalChannel => eventService subscribe it
      case _                 =>
    }
  }
}
