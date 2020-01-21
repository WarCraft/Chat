package gg.warcraft.chat.profile

import gg.warcraft.monolith.api.util.ColorCode

case class ChatTag(
    title: String,
    color: ColorCode
) {
  def format: String = s"$color$title"
}
