package gg.warcraft.chat

import gg.warcraft.monolith.api.core.event.{CancellableEvent, Event}
import gg.warcraft.monolith.api.player.Player

case class AsyncPlayerPreChatEvent(
    player: Player,
    text: String,
    cancelled: Boolean = false,
    explicitlyAllowed: Boolean = false
) extends CancellableEvent

case class AsyncPlayerChatEvent(
    player: Player,
    text: String
) extends Event
