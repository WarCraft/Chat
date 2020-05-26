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

import gg.warcraft.chat.AsyncPlayerPreChatEvent
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.player.PlayerService
import org.bukkit.event.{EventHandler, Listener}

class SpigotChatEventMapper(implicit
    eventService: EventService,
    playerService: PlayerService
) extends Listener {
  @EventHandler
  def preChat(event: SpigotAsyncPlayerChatEvent): Unit = {
    val player = playerService.getPlayer(event.getPlayer.getUniqueId)
    val preEvent = AsyncPlayerPreChatEvent(player, event.getMessage)
    eventService.publish(preEvent)
    event.setCancelled(true)
  }
}
