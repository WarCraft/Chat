package gg.warcraft.chat.akka.profile

import java.util.UUID

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.monolith.api.core.event.EventService
import io.getquill.context.jdbc.JdbcContext

object ProfileService {
  sealed trait Command

  /**  */
  final case class ValidateProfile(playerId: UUID, name: String) extends Command

  /**  */
  private[profile] final case class CacheProfile(profile: Profile) extends Command

  /**  */
  private[profile] final case class CreateProfile(
      playerId: UUID,
      name: String
  ) extends Command

  /**  */
  private[profile] final case class UpdateProfile(profile: Profile) extends Command

  /**  */
  final case class RequestProfile(
      playerId: UUID,
      replyTo: ActorRef[ProfileResult]
  ) extends Command

  /**  */
  final case class ProfileResult(profile: Profile)

  /** Creates a new profile service actor. Parent actors need to create and subscribe
    * a profile handler to enable normal operation. */
  def apply(
      defaultTag: String,
      defaultChannel: String
  )(
      implicit database: JdbcContext[_, _],
      eventService: EventService
  ): Behavior[Command] = Behaviors.setup { context =>
    context.spawn(ProfileHandler(context.self), "handler")

    import database._
    def ProfileService(profiles: Map[UUID, Profile]): Behavior[Command] =
      Behaviors.receive { (context, message) =>
        message match {
          case ValidateProfile(playerId, name) =>
            database
              .run(query[Profile].filter(_.playerId == lift(playerId)))
              .headOption match {
              case Some(profile) =>
                if (profile.name != name) UpdateProfile(profile.copy(name = name))
                else CacheProfile(profile)
              case None => CreateProfile(playerId, name)
            }
            Behaviors.same

          case CacheProfile(profile) =>
            ProfileService(profiles + (profile.playerId -> profile))

          case CreateProfile(playerId, name) =>
            val profile = Profile(playerId, name, defaultTag, defaultChannel)
            database.run(query[Profile].insert(lift(profile)))
            ProfileService(profiles + (profile.playerId -> profile))

          case UpdateProfile(profile) =>
            database.run(query[Profile].update(lift(profile)))
            ProfileService(profiles + (profile.playerId -> profile))

          case RequestProfile(playerId, replyTo) =>
            val profile = profiles(playerId)
            replyTo ! ProfileResult(profile)
            Behaviors.same
        }
      }

    ProfileService(Map.empty)
  }
}
