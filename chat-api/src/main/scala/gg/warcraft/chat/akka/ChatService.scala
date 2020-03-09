package gg.warcraft.chat.akka

import java.util.UUID

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.chat.ChatHandler

object ChatService {
  sealed trait Command

  /** */
  final case class RegisterHandler(
      playerId: UUID,
      replyTo: ActorRef[PlayerInput]
  ) extends Command

  /** */
  final case class PlayerInput(input: Option[String])

  final case class RouteMessage(player: Player,  text: String, channel: Option[String] = None) extends Command

  /** */
  def apply(handlers: Map[UUID, ActorRef[PlayerInput]]): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case RegisterHandler(playerId, replyTo) =>
          ChatService(handlers + (playerId -> replyTo))

        case RouteMessage(player, text) =>
          /*
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
           */
      }
    }
}
