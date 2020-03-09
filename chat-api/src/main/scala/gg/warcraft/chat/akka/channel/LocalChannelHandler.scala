package gg.warcraft.chat.akka.channel

import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.monolith.api.core.command.{
  Command, CommandHandler, CommandSender, CommandService
}

private[this] class LocalChannelCommandHandler(
    channel: ActorRef[LocalChannel.Command]
) extends CommandHandler {
  private final val WARN_CONSOLE_LOCAL_CHANNEL =
    "Only players can talk in local channels!"

  override def handle(sender: CommandSender, cmd: Command): Boolean = {
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

object LocalChannelHandler {
  def apply(channel: ActorRef[LocalChannel.Command])(
      implicit commandService: CommandService
  ): Behavior[Unit] = {
    val commandHandler = new LocalChannelCommandHandler(channel)
    commandService subscribe commandHandler

    Behaviors.receiveSignal {
      case (_, PostStop) =>
        commandService unsubscribe commandHandler
        Behaviors.stopped

      case _ => Behaviors.same
    }
  }
}
