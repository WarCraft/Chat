/*
 * MIT License
 *
 * Copyright (c) 2020 WarCraft <https://github.com/WarCraft>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
      channel.format
        .replaceAll("\\$\\{channel\\.color}", channel.color.toString)
        .replaceAll("\\$\\{channel\\.name}", channel.name)
        .replaceAll("\\$\\{sender\\.tag}", sender.tag)
        .replaceAll("\\$\\{sender\\.name}", sender.name)
        .replaceAll("\\$\\{text}", text)
    )
}

class ChatMessage(
    val channel: Channel,
    val sender: Option[Profile],
    original: String,
    formatted: String
) extends Message(original, formatted)
