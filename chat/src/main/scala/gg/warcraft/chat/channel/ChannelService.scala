package gg.warcraft.chat.channel

import gg.warcraft.chat.ChatConfig
import gg.warcraft.monolith.api.Implicits
import gg.warcraft.monolith.api.core.command.{Command, CommandPreExecuteEvent}
import gg.warcraft.monolith.api.core.event.{EventHandler, EventService, PreEvent}

import scala.collection.mutable

object ChannelService extends EventHandler {
  var eventService: EventService = Implicits.eventService // TODO replace

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
    _channelsByAlias += (channel.name.toLowerCase -> channel)
    channel.aliases.foreach(it => _channelsByAlias += (it.toLowerCase -> channel))
    channel.shortcut.map(it => _channelsByShortcut += (it.toLowerCase -> channel))

    channel match {
      case it: GlobalChannel => eventService.subscribe(it)
      case _                 => ()
    }
  }

  override def reduce[T >: PreEvent](event: T): T = event match {
    case CommandPreExecuteEvent(sender, cmd, label, args, _, _) =>
      _channelsByAlias.get(label) match {
        case Some(channel) =>
          channel.handle(sender, Command(cmd, label, args))
          event.asInstanceOf[CommandPreExecuteEvent].copy(cancelled = true)
        case None => event
      }

    case _ => event
  }
}
