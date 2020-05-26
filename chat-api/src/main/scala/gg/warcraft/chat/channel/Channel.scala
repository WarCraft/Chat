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

package gg.warcraft.chat.channel

import java.util.UUID

import gg.warcraft.chat.message.{ChatMessage, MessageAdapter}
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.api.core.{ColorCode, Message}
import gg.warcraft.monolith.api.core.auth.Principal
import gg.warcraft.monolith.api.core.command.Command
import gg.warcraft.monolith.api.util.chaining._

trait Channel extends Command.Handler {
  private final val homeMessage = ChatMessage.home(this)

  val name: String
  val aliases: Set[String]
  val shortcut: Option[String]
  val color: ColorCode
  val format: String

  val command: Command = Command(name, aliases.toList, "")

  def broadcast(
      sender: Principal,
      text: String,
      recipients: Iterable[UUID]
  )(implicit
      profileService: ProfileService,
      messageAdapter: MessageAdapter
  ): Unit = {
    val message =
      if (sender.isPlayer) {
        val profile = profileService.profiles(sender.id)
        ChatMessage(this, profile, text)
      } else Message.server(text)

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
