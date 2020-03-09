package gg.warcraft.chat.akka.channel

import akka.actor.typed.ActorRef
import gg.warcraft.monolith.api.core.command.{Command, CommandHandler, CommandSender}

class LocalChannelHandler(
    channel: ActorRef[LocalChannel.Command]
) extends CommandHandler {
  private final val WARN_CONSOLE_LOCAL_CHANNEL =
    "Only players can talk in local channels!"

  override def handle(sender: CommandSender, cmd: Command): Boolean = sender match {
    case CommandSender(_, Some(playerId)) =>
      if (!cmd.args.isEmpty) {
        val message = cmd.args.mkString(" ")
        channel ! LocalChannel.SendMessage(sender.ref, message)
      } else {} // TODO set player home channel
      true

    case CommandSender(_, None) =>
      sender.ref ! MessageAble.SendMessage(WARN_CONSOLE_LOCAL_CHANNEL)
      true
  }
}
