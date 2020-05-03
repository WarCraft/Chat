package gg.warcraft.chat.message

import java.util.UUID

import gg.warcraft.monolith.api.core.Message

trait MessageAdapter {
  def broadcast(message: Message): Unit

  def broadcastStaff(message: Message): Unit

  def send(message: Message, playerId: UUID): Unit
}
