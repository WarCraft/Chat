package gg.warcraft.chat

import java.util.UUID

trait ChatHandler {
  def handle(playerId: UUID, text: String): Boolean
}
