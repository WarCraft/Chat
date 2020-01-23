package gg.warcraft.chat

import java.util.UUID

import gg.warcraft.chat.channel.ChannelRepository
import gg.warcraft.chat.profile.ChatProfileRepository
import gg.warcraft.monolith.api.core.TaskService
import gg.warcraft.monolith.api.core.command.{Command, CommandSender}
import gg.warcraft.monolith.api.core.event.{EventHandler, PreEvent}

import scala.collection.mutable

object ChatService {
  private val handlers = mutable.Map[UUID, ChatHandler]()
}

class ChatService(
    private implicit val taskService: TaskService,
    private implicit val channelRepo: ChannelRepository,
    private implicit val profileRepo: ChatProfileRepository
) extends EventHandler {
  import ChatService.handlers
  import channelRepo.{channelsByName, defaultChannel, findChannelForShortcut}
  import profileRepo.profiles

  def register(handler: ChatHandler, playerId: UUID): Unit =
    handlers += (playerId -> handler)

  def broadcast(message: Message): Unit = {
    // TODO send message to all online players
  }

  def broadcastStaff(message: Message): Unit = {
    // TODO send message to all online players with staff permission
  }

  override def reduce[T <: PreEvent](event: T): T = event match {
    case it: AsyncPlayerPreChatEvent => reduce(it).asInstanceOf[T]
    case _                           => event
  }

  private def reduce(event: AsyncPlayerPreChatEvent): AsyncPlayerPreChatEvent = {
    import event.{name, playerId, text}

    handlers.get(playerId) match {
      case Some(handler) =>
        // run on next sync tick as chat events are async
        taskService.runNextTick(() => {
          if (handler.handle(playerId, text)) handlers -= playerId
        })

      case None =>
        var trimmedText = text
        val channel = findChannelForShortcut(text) match {
          case Some(channel) =>
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
}
