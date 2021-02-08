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

package gg.warcraft.chat.spigot

import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.monolith.api.core.Message
import gg.warcraft.monolith.api.core.auth.AuthService
import gg.warcraft.monolith.api.player.PlayerService
import gg.warcraft.monolith.api.util.chaining._
import org.bukkit.Server

import java.util.UUID
import java.util.logging.Logger
import scala.jdk.CollectionConverters._

class SpigotMessageAdapter(implicit
    server: Server,
    logger: Logger,
    authService: AuthService,
    playerService: PlayerService
) extends MessageAdapter {
  override def broadcast(message: Message): Unit =
    server.getOnlinePlayers.forEach { _.sendMessage(message.formatted) }

  override def broadcastStaff(message: Message): Unit =
    server.getOnlinePlayers.asScala
      .map { _.getUniqueId |> playerService.getPlayer }
      .filter { authService.isStaff }
      .foreach { _.sendMessage(message) }

  override def send(message: Message, playerId: UUID): Unit =
    playerService.getPlayer(playerId).sendMessage(message)
}
