package gg.warcraft.chat.akka

import java.time.LocalTime
import java.util.UUID

import akka.actor.typed.ActorRef
import gg.warcraft.monolith.api.util.ColorCode

object Player {
  sealed trait Command

  /** */
  final case class SendMessage(text: String) extends Command

  /** */
  final case class SendServerMessage(text: String) extends Command {
    private final val SERVER_MESSAGE_FORMAT = s"${ColorCode.YELLOW}[Server] %s"
    val formatted: String = SERVER_MESSAGE_FORMAT.format(text)
  }

  /** */
  final case class SendErrorMessage(code: String) extends Command {
    private final val ERR_MESSAGE_FORMAT =
      "Internal Error: Please notify an admin you ran into error code %s."
    val formatted: String = {
      val error = ERR_MESSAGE_FORMAT.format(s"$code:${LocalTime.now}")
      SendServerMessage(error).formatted
    }
  }

  /** */
  final case class ConfirmPermissions(
      permissions: List[String],
      replyTo: ActorRef[PermissionsConfirmation]
  ) extends Command

  /** */
  final case class PermissionsConfirmation(permissions: List[String]) {
    def contains(permission: String): Boolean = permissions contains permission
  }

  implicit def toActorRef(p: Player): ActorRef[Player.Command] = p.ref
}

case class Player(
    id: UUID,
    name: String
)(
    val ref: ActorRef[Player.Command]
) {
  def !(msg: Player.Command): Unit = ref ! msg

  override def hashCode(): Int = id.hashCode()
}
