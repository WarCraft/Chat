package gg.warcraft.chat

import java.util.UUID

import gg.warcraft.chat.channel.{Channel, ChannelService}
import gg.warcraft.chat.profile.{ChatProfile, ChatProfileService}
import gg.warcraft.monolith.api.core.command.{Command, CommandSender}
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}

import scala.collection.mutable

object ChatService extends EventHandler {
  private val handlers = mutable.Map[UUID, ChatHandler]()

  def register(handler: ChatHandler, playerId: UUID): Unit =
    handlers += (playerId -> handler)

  override def handle(event: Event): Unit = event match {
    case AsyncPlayerChatEvent(playerId, text) =>
      handlers.get(playerId) match {
        case Some(handler) =>
          // run on next sync tick as chat events are async
          taskService.runNextTick(() -> {
            if (handler.handle(playerId, text)) handlers -= playerId
          })

        case None =>
          var channel: Channel = null
          var trimmedText = text
          ChannelService.findChannelForShortcut(text) match {
            case Some(it) =>
              channel = it
              trimmedText = text.substring(it.shortcut.get.length).trim
            case None =>
              channel = ChatProfileService.profiles(playerId) match {
                case ChatProfile(_, _, _, Some(homeChannel), _) =>
                  ChannelService.channelsByAlias(homeChannel)
                case _ => ChannelService.defaultChannel
              }
          }

          val command = channel.name.toLowerCase
          channel.handle(
            CommandSender("", Some(null)),
            Command(command, command, text.split(" "))
          )
      }

    case _ => ()
  }
}
