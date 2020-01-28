package gg.warcraft.chat.message

import gg.warcraft.chat.channel.Channel
import gg.warcraft.chat.profile.ChatProfile
import gg.warcraft.monolith.api.util.ColorCode

object Message {
  private final val muteText = "No one heard you!"
  def mute: Message =
    Message(null, None, muteText, s"${ColorCode.GRAY}$muteText")

  def server(text: String): Message =
    Message(null, None, text, s"${ColorCode.YELLOW}[Server] $text")

  def apply(channel: Channel, sender: ChatProfile, text: String): Message = Message(
    channel,
    Some(sender),
    text,
    channel.color + channel.formatString
      .replaceAll("<channel\\.name>", channel.name)
      .replaceAll("<channel\\.color>", channel.color.toString)
      .replaceAll("<sender\\.name>", sender.name)
      .replaceAll("<sender\\.tag>", sender.tag)
      .replaceAll("<text>", text)
  )
}

case class Message(
    channel: Channel,
    sender: Option[ChatProfile],
    original: String,
    formatted: String
)
