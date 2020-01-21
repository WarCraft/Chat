package gg.warcraft.chat.message

import gg.warcraft.chat.channel.Channel
import gg.warcraft.chat.profile.{ChatProfile, ChatTag}
import gg.warcraft.monolith.api.util.ColorCode

object Message {
  private val consoleSender = ChatProfile.console
  private val consoleColor = ChatTag.console.color
  private val consoleTitle = ChatTag.console.title

  def apply(channel: Channel, sender: ChatProfile, text: String): Message = Message(
    channel,
    sender,
    text,
    channel.color + channel.formatString
      .replaceAll("<channel\\.name>", channel.name)
      .replaceAll("<channel\\.color>", channel.color.toString)
      .replaceAll("<sender\\.name>", sender.name)
      .replaceAll("<sender\\.tag>", sender.tag.get.title)
      .replaceAll("<sender\\.color>", sender.tag.get.color.toString)
      .replaceAll("<text>", text)
  )

  def console(text: String, formattedText: String): Message =
    Message(null, consoleSender, text, formattedText)

  private final val muteText = "No one heard you!"
  def mute: Message =
    Message(null, consoleSender, muteText, s"${ColorCode.GRAY}$muteText")

  def server(text: String): Message =
    Message(null, consoleSender, text, s"$consoleColor[$consoleTitle] $text")
}

case class Message(
    channel: Channel,
    sender: ChatProfile,
    original: String,
    formatted: String
)
