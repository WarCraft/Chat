package gg.warcraft.chat.message

import gg.warcraft.chat.channel.Channel
import gg.warcraft.chat.profile.Profile
import gg.warcraft.monolith.api.core.{ColorCode, Message}

object ChatMessage {
  def home(channel: Channel): Message =
    Message.server(s"${channel.name} is now your home channel.")

  private final val muteText = "No one heard you!"
  def mute: Message = Message(muteText, s"${ColorCode.GRAY}$muteText")

  def apply(channel: Channel, sender: Profile, text: String): Message =
    new ChatMessage(
      channel,
      Some(sender),
      text,
      channel.color + channel.format
        .replaceAll("<channel\\.name>", channel.name)
        .replaceAll("<channel\\.color>", channel.color.toString)
        .replaceAll("<sender\\.name>", sender.name)
        .replaceAll("<sender\\.tag>", sender.tag)
        .replaceAll("<text>", text)
    )
}

class ChatMessage(
    val channel: Channel,
    val sender: Option[Profile],
    original: String,
    formatted: String
) extends Message(original, formatted)
