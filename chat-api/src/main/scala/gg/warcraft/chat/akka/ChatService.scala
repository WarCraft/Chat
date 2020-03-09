package gg.warcraft.chat.akka

import java.util.UUID

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.chat.ChatHandler

object ChatService {
  sealed trait Command
  final case class RegisterHandler() extends Command

  def apply(handlers: Map[UUID, ChatHandler]): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case RegisterHandler() => Behaviors.same
      }
    }
}
