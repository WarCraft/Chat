package gg.warcraft.chat

import java.util.UUID

import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.api.core.command.{ Command, CommandPreExecuteEvent }
import gg.warcraft.monolith.api.core.event.{ Event, PreEvent }
import gg.warcraft.monolith.api.core.task.{ Task, TaskService }
import gg.warcraft.monolith.api.util.Ops._

class ChatService(
    implicit taskService: TaskService,
    channelService: ChannelService,
    profileService: ProfileService
) extends Event.Handler {
  import channelService.{channelsByName, channelsByShortcut, defaultChannel}
  import profileService.profiles

  private var handlers: Map[UUID, ChatHandler] = Map.empty

  def register(handler: ChatHandler, playerId: UUID): Unit =
    handlers += (playerId -> handler)

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
        taskService.evalNextTick {
          if (handler.handle(playerId, text)) handlers -= playerId
        }

      case None =>
        var trimmedText = text
        val channel = channelsByShortcut.find(_._1 |> text.startsWith) match {
          case Some((_, channel)) =>
            trimmedText = text.substring(channel.shortcut.get.length).trim
            channel

          case None =>
            val homeChannel = profiles(playerId).home
            channelsByName.getOrElse(homeChannel, defaultChannel)
        }

        val command = channel.name.toLowerCase
        channel.handle(
          // TODO send player object (principal) with pre chat event
          CommandSender(name, Some(playerId)),
          Command(command, command, trimmedText)

        )
    }

    event.copy(cancelled = true)
  }

  private def reduce(event: CommandPreExecuteEvent): CommandPreExecuteEvent = {
    import event.{args, command, principal}
    channelService.channelsByAlias.get(command.name.toLowerCase) match {
      case Some(channel) =>
        channel.handle(principal, command, args: _*)
        event.copy(cancelled = true)
      case None => event
    }
  }
}
