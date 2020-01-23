package gg.warcraft.chat.channel

import java.util.UUID

import gg.warcraft.chat.Message
import gg.warcraft.chat.profile.ChatProfileRepository
import gg.warcraft.monolith.api.core.command.{CommandHandler, CommandSender}
import gg.warcraft.monolith.api.util.ColorCode

trait Channel extends CommandHandler {
  val name: String
  val aliases: Set[String]
  val shortcut: Option[String]
  val color: ColorCode
  val formatString: String

  protected implicit val profileRepo: ChatProfileRepository

  def broadcast(
      sender: CommandSender,
      text: String,
      recipients: Iterable[UUID]
  ): Unit = {
    val message = sender match {
      case CommandSender(_, Some(playerId)) =>
        val profile = profileRepo.profiles(playerId)
        Message(this, profile, text)
      case _ => Message.server(text)
    }

    recipients.foreach(_ /* TODO send message to all recipients */ )
    if (recipients.size == 1 && sender.isPlayer) {
      // TODO send mute message to player
    }

    // NOTE option to log message here
  }

  def makeHome(playerId: UUID): Boolean = {
    val profile = profileRepo.profiles(playerId)
    if (profile.home != name) {
      profileRepo.save(profile.copy(home = name))
      // TODO send join message to player
      true
    } else false
  }
}
