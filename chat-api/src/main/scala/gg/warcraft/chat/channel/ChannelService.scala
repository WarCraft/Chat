package gg.warcraft.chat.channel

import gg.warcraft.chat.ChatConfig

import scala.collection.mutable

object ChannelService {
  private val _channels = mutable.ListBuffer[Channel]()
  private val _channelsByAlias = mutable.Map[String, Channel]()
  private val _channelsByShortcut = mutable.Map[String, Channel]()

  private var _defaultChannel: Channel = _

  def channels: List[Channel] =
    _channels.asInstanceOf[List[Channel]]

  def channelsByAlias: Map[String, Channel] =
    _channelsByAlias.asInstanceOf[Map[String, Channel]]

  def channelsByShortcut: Map[String, Channel] =
    _channelsByShortcut.asInstanceOf[Map[String, Channel]]

  def findChannelForShortcut(text: String): Option[Channel] =
    _channelsByShortcut.values.find(it => text.startsWith(it.shortcut))

  def defaultChannel: Channel = _defaultChannel

  def read(config: ChatConfig): Unit = {
    config.globalChannels.foreach(register)
    config.localChannels.foreach(register)
    _defaultChannel = _channelsByAlias(config.defaultChannel)
  }

  def register(channel: Channel): Unit = {
    _channels += channel
    _channelsByAlias += (channel.name -> channel)
    channel.aliases.foreach(it => _channelsByAlias += (it -> channel))
    channel.shortcut.map(it => _channelsByShortcut += (it -> channel))
  }
}
