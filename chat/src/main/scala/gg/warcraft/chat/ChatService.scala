package gg.warcraft.chat

import java.util.UUID

import gg.warcraft.chat.channel.ChannelService
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.monolith.api.core.command.{Command, CommandSender}
import gg.warcraft.monolith.api.core.event.{EventHandler, PreEvent}

import scala.collection.mutable

object ChatService extends EventHandler {
  private val handlers = mutable.Map[UUID, ChatHandler]()

  def register(handler: ChatHandler, playerId: UUID): Unit =
    handlers += (playerId -> handler)

  def broadcast(message: Message): Unit = {
    // TODO send message to all online players
  }

  def broadcastStaff(message: Message): Unit = {
    // TODO send message to all online players with staff permission
  }

  override def reduce[T <: PreEvent](event: T): T = event match {
    case event: AsyncPlayerPreChatEvent =>
      import event.{name, playerId, text}

      handlers.get(playerId) match {
        case Some(handler) =>
          // run on next sync tick as chat events are async
          taskService.runNextTick(() -> {
            if (handler.handle(playerId, text)) handlers -= playerId
          })

        case None =>
          var trimmedText = text
          val channel = ChannelService.findChannelForShortcut(text) match {
            case Some(channel) =>
              trimmedText = text.substring(channel.shortcut.get.length).trim
              channel
            case None =>
              ChannelService.channelsByName.getOrElse(
                ChatProfileService.profiles(playerId).homeChannel,
                ChannelService.defaultChannel
              )
          }

          val command = channel.name.toLowerCase
          channel.handle(
            CommandSender(name, Some(playerId)),
            Command(command, command, text.split(" "))
          )
      }

      event.copy(cancelled = true).asInstanceOf[T]

    case _ => event
  }
}
