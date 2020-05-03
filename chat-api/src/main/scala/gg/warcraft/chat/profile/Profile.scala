package gg.warcraft.chat.profile

import java.util.UUID

case class Profile(
    playerId: UUID,
    name: String,
    tag: String,
    home: String
)
