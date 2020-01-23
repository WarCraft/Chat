package gg.warcraft.chat.channel

import gg.warcraft.monolith.api.core.command.{Command, CommandPreExecuteEvent}
import gg.warcraft.monolith.api.core.event.{EventHandler, PreEvent}

class ChannelEventHandler(
    private implicit val channelRepo: ChannelRepository
) extends EventHandler {
  override def reduce[T <: PreEvent](event: T): T = event match {
    case preExecute: CommandPreExecuteEvent =>
      import preExecute.{args, cmd, label, sender}

      channelRepo.channelsByAlias.get(label) match {
        case Some(channel) =>
          channel.handle(sender, Command(cmd, label, args))
          preExecute.copy(cancelled = true).asInstanceOf[T]
        case None => event
      }

    case _ => event
  }
}
