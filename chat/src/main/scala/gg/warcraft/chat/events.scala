package gg.warcraft.chat

import java.util.UUID

import gg.warcraft.monolith.api.core.event.{CancellableEvent, Event}

case class AsyncPlayerPreChatEvent(
    playerId: UUID,
    name: String,
    text: String,
    cancelled: Boolean = false,
    explicitlyAllowed: Boolean = false
) extends CancellableEvent

case class AsyncPlayerChatEvent(
    playerId: UUID,
    name: String,
    text: String
) extends Event
