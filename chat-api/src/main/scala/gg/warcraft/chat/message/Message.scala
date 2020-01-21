package gg.warcraft.chat.message

import gg.warcraft.chat.channel.Channel
import gg.warcraft.chat.profile.ChatTag
import gg.warcraft.monolith.api.core.command.CommandSender
import gg.warcraft.monolith.api.util.ColorCode

object Message {
  private val consoleSender = CommandSender.console
  private val consoleColor = ChatTag.console.color
  private val consoleTitle = ChatTag.console.title

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
    sender: CommandSender,
    original: String,
    formatted: String
)
