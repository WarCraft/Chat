package gg.warcraft.chat

import java.util.UUID

import gg.warcraft.chat.channel.Channel
import gg.warcraft.monolith.api.core.event.{CancellableEvent, Event}

case class AsyncPlayerPreChatEvent(
    playerId: UUID,
    text: String,
    cancelled: Boolean = false,
    explicitlyAllowed: Boolean = false
) extends CancellableEvent

case class AsyncPlayerChatEvent(
    playerId: UUID,
    text: String
) extends Event

case class PlayerHomeChannelChangeEvent(
    playerId: UUID,
    channel: Channel
) extends Event
