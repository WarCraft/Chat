package gg.warcraft.chat.channel

import gg.warcraft.chat.ChatConfig
import gg.warcraft.monolith.api.core.event.EventService

import scala.collection.mutable

object ChannelService {
  private val _channels = mutable.ListBuffer[Channel]()
  private val _channelsByName = mutable.Map[String, Channel]()
  private val _channelsByAlias = mutable.Map[String, Channel]()
  private val _channelsByShortcut = mutable.Map[String, Channel]()

  private var _defaultChannel: Channel = _
}

class ChannelService(
    private implicit val eventService: EventService
) {
  import ChannelService._

  def channels: List[Channel] = _channels.toList
  def channelsByName: Map[String, Channel] = _channelsByName.toMap
  def channelsByAlias: Map[String, Channel] = _channelsByAlias.toMap
  def channelsByShortcut: Map[String, Channel] = _channelsByShortcut.toMap
  def defaultChannel: Channel = _defaultChannel

  def readConfig(config: ChatConfig): Unit = {
    val newChannels = mutable.ListBuffer[Channel]()
    val newChannelsByName = mutable.Map[String, Channel]()
    val newChannelsByAlias = mutable.Map[String, Channel]()
    val newChannelsByShortcut = mutable.Map[String, Channel]()

    (config.globalChannels ++ config.localChannels).foreach(channel => {
      newChannels.addOne(channel)
      newChannelsByName.put(channel.name, channel)
      newChannelsByAlias.put(channel.name.toLowerCase, channel)
      channel.aliases.foreach(newChannelsByAlias.put(_, channel))
      channel.shortcut.map(newChannelsByShortcut.put(_, channel))
    })

    // only after successfully setting the default channel using the
    // volatile accessor do we overwrite the active channel collections
    _defaultChannel = _channelsByName(config.defaultChannel)

    _channels.foreach {
      case it: GlobalChannel => eventService.unsubscribe(it)
      case _                 => ()
    }

    _channels.clear()
    _channels.addAll(newChannels)
    _channelsByName.clear()
    _channelsByName.addAll(newChannelsByName)
    _channelsByAlias.clear()
    _channelsByAlias.addAll(newChannelsByAlias)
    _channelsByShortcut.clear()
    _channelsByShortcut.addAll(newChannelsByShortcut)

    config.globalChannels.foreach(eventService.subscribe)
  }
}
