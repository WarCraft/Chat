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

import java.util.logging.Logger

import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.api.core.auth.Principal
import gg.warcraft.monolith.api.core.command.Command
import gg.warcraft.monolith.api.core.ColorCode
import gg.warcraft.monolith.api.entity.EntityService
import gg.warcraft.monolith.api.player.{Player, PlayerService}

case class LocalChannel(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode,
    format: String,
    radius: Float
)(implicit
    logger: Logger,
    entityService: EntityService,
    playerService: PlayerService,
    profileService: ProfileService,
    messageAdapter: MessageAdapter
) extends Channel {
  override def handle(
      sender: Principal,
      command: Command,
      args: String*
  ): Command.Result = sender match {
    case player: Player =>
      if (args.isEmpty) {
        makeHome(player.id)
        Command.success
      } else {
        val recipients = entityService
          .getNearbyEntities(player.location, radius)
          .filter { _.isInstanceOf[Player] }
          .map { _.id }
        broadcast(sender, args.mkString(" "), recipients)
        Command.success
      }
    case _ => Command.playersOnly
  }
}
