package gg.warcraft.chat.message

import java.util.UUID

trait MessageAdapter {
  def broadcast(message: Message): Unit

  def broadcastStaff(message: Message): Unit

  def send(message: Message, playerId: UUID): Unit
}
