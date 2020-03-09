package gg.warcraft.chat.akka

import akka.actor.typed.ActorRef
import gg.warcraft.chat.AsyncPlayerPreChatEvent
import gg.warcraft.chat.ChatService.handlers
import gg.warcraft.monolith.api.core.command.{
  Command, CommandPreExecuteEvent, CommandSender
}
import gg.warcraft.monolith.api.core.event.{EventHandler, PreEvent}

class ChatHandler(
    service: ActorRef[ChatService.Command]
) extends EventHandler {
  override def reduce[T <: PreEvent](event: T): T = event match {
    case AsyncPlayerPreChatEvent(playerId, name, text, _, _) =>
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

      event.copy(cancelled = true).asInstanceOf[T]

    case CommandPreExecuteEvent(sender, cmd, label, args, _, _)  =>
      channelService.channelsByAlias.get(label) match {
        case Some(channel) =>
          channel.handle(sender, Command(cmd, label, args))
          event.copy(cancelled = true).asInstanceOf[T]

        case None => event
      }

    case _                           => event
  }
}
