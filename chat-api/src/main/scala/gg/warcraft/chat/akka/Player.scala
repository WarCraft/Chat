package gg.warcraft.chat.akka

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Player {
  sealed trait Command
  final case class SendMessage(message: String) extends Command

  // TODO move to Spigot layer as SpigotPlayer, and this probably belongs in Monolith
  def apply(schedule: String => Unit): Behavior[Command] =
    Behaviors.receiveMessage {
      // TODO add asynchronous safe method to ServerAdapter which cleans queue every tick
      case SendMessage(message) => schedule(message); Behaviors.same
    }
}
