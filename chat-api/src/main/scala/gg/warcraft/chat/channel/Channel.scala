package gg.warcraft.chat.channel

import java.util.UUID

import gg.warcraft.chat.message.{ChatMessage, MessageAdapter}
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.api.core.{ColorCode, Message}
import gg.warcraft.monolith.api.core.auth.Principal
import gg.warcraft.monolith.api.core.command.Command
import gg.warcraft.monolith.api.util.Ops._

trait Channel extends Command.Handler {
  private final val homeMessage = ChatMessage.home(this)

  val name: String
  val aliases: Set[String]
  val shortcut: Option[String]
  val color: ColorCode.Type
  val format: String

  def broadcast(
      sender: Principal,
      text: String,
      recipients: Iterable[UUID]
  )(implicit
    profileService: ProfileService,
    messageAdapter: MessageAdapter
  ): Unit = {
    val message = sender.principalId match {
      case Some(playerId) =>
        val profile = profileService.profiles(playerId)
        ChatMessage(this, profile, text)
      case _ => Message.server(text)
    }

    recipients.foreach { messageAdapter.send(message, _) }
    if (recipients.size == 1) sender.sendMessage(ChatMessage.mute)

    // NOTE option to log message here
  }

  def makeHome(
      playerId: UUID
  )(implicit
    profileService: ProfileService,
    messageAdapter: MessageAdapter
  ): Unit = {
    val profile = profileService.profiles(playerId)
    if (profile.home != name) {
      profile.copy(home = name) |> profileService.saveProfile
      messageAdapter.send(homeMessage, playerId)
    }
  }
}
