package gg.warcraft.chat.channel

import scala.collection.mutable

object ChannelRepository {
  private val _channels = mutable.ListBuffer[Channel]()
  private val _channelsByName = mutable.Map[String, Channel]()
  private val _channelsByAlias = mutable.Map[String, Channel]()
  private val _channelsByShortcut = mutable.Map[String, Channel]()

  private var _defaultChannel: Channel = _
}

class ChannelRepository {
  import ChannelRepository._

  def channels: List[Channel] =
    _channels.asInstanceOf[List[Channel]]

  def channelsByName: Map[String, Channel] =
    _channelsByName.asInstanceOf[Map[String, Channel]]

  def channelsByAlias: Map[String, Channel] =
    _channelsByAlias.asInstanceOf[Map[String, Channel]]

  def channelsByShortcut: Map[String, Channel] =
    _channelsByShortcut.asInstanceOf[Map[String, Channel]]

  def findChannelForShortcut(text: String): Option[Channel] =
    _channelsByShortcut.values.find(it => text.startsWith(it.shortcut))

  def defaultChannel: Channel = _defaultChannel

  def save(channel: Channel, default: Boolean = false): Unit = {
    _channels += channel
    _channelsByName += (channel.name -> channel)
    _channelsByAlias += (channel.name.toLowerCase -> channel)
    channel.aliases.foreach(it => _channelsByAlias += (it.toLowerCase -> channel))
    channel.shortcut.map(it => _channelsByShortcut += (it -> channel))

    if (default) _defaultChannel = channel
  }
}
