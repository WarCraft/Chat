package gg.warcraft.chat

import java.util.UUID

import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.monolith.api.core.TaskService
import gg.warcraft.monolith.api.core.command.{
  Command, CommandPreExecuteEvent, CommandSender
}
import gg.warcraft.monolith.api.core.event.{EventHandler, PreEvent}

import scala.collection.mutable

object ChatService {
  private val handlers = mutable.Map[UUID, ChatHandler]()
}

class ChatService(
    private implicit val taskService: TaskService,
    private implicit val channelService: ChannelService,
    private implicit val profileService: ChatProfileService
) extends EventHandler {
  import ChatService.handlers
  import channelService.{channelsByName, channelsByShortcut, defaultChannel}
  import profileService.profiles

  def register(handler: ChatHandler, playerId: UUID): Unit =
    handlers.put(playerId, handler)

  override def reduce[T <: PreEvent](event: T): T = event match {
    case it: AsyncPlayerPreChatEvent => reduce(it).asInstanceOf[T]
    case it: CommandPreExecuteEvent  => reduce(it).asInstanceOf[T]
    case _                           => event
  }

  private def reduce(event: AsyncPlayerPreChatEvent): AsyncPlayerPreChatEvent = {
    import event.{name, playerId, text}

    handlers.get(playerId) match {
      case Some(handler) =>
        // run on next sync tick as chat events are async
        taskService.runNextTick(() => {
          if (handler.handle(playerId, text)) handlers.subtractOne(playerId)
        })

      case None =>
        var trimmedText = text
        val channel = channelsByShortcut.find(it => text.startsWith(it._1)) match {
          case Some((_, channel)) =>
            trimmedText = text.substring(channel.shortcut.get.length).trim
            channel

          case None =>
            channelsByName.getOrElse(
              profiles(playerId).home,
              defaultChannel
            )
        }

        val command = channel.name.toLowerCase
        channel.handle(
          CommandSender(name, Some(playerId)),
          Command(command, command, text.split(" "))
        )
    }

    event.copy(cancelled = true)
  }

  private def reduce(event: CommandPreExecuteEvent): CommandPreExecuteEvent = {
    import event.{args, cmd, label, sender}

    channelService.channelsByAlias.get(label) match {
      case Some(channel) =>
        channel.handle(sender, Command(cmd, label, args))
        event.copy(cancelled = true)

      case None => event
    }
  }
}
