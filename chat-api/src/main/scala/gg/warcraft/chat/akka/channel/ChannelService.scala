package gg.warcraft.chat.akka.channel

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.chat.{GlobalChannelConfig, LocalChannelConfig}
import gg.warcraft.chat.akka.profile.ProfileService
import gg.warcraft.monolith.api.core.command.CommandService
import gg.warcraft.monolith.api.core.event.EventService

object ChannelService {
  sealed trait Command
  final case class CreateGlobalChannel(config: GlobalChannelConfig) extends Command
  final case class CreateLocalChannel(config: LocalChannelConfig) extends Command

  def apply(
      defaultChannel: String
  )(
      implicit profileService: ActorRef[ProfileService.Command],
      commandService: CommandService,
      eventService: EventService
  ): Behavior[Command] = {
    def ChannelService(
        globalChannels: List[ActorRef[GlobalChannel.Command]],
        localChannels: List[ActorRef[LocalChannel.Command]]
    ): Behavior[Command] = Behaviors.receive { (context, message) =>
      message match {
        case CreateGlobalChannel(config) =>
          import config._
          val channel =
            GlobalChannel(name, aliases, shortcut, color, format, permission)
          ChannelService(
            context.spawn(channel, name) :: globalChannels,
            localChannels
          )
        case CreateLocalChannel(config) =>
          import config._
          val channel =
            LocalChannel(name, aliases, shortcut, color, format, radius)
          ChannelService(
            globalChannels,
            context.spawn(channel, name) :: localChannels
          )
      }
    }

    ChannelService(Nil, Nil)
  }
}
