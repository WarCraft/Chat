package gg.warcraft.chat.profile

import gg.warcraft.monolith.api.util.ColorCode

object ChatTag {
  val console: ChatTag = ChatTag("Server", ColorCode.YELLOW)
}

case class ChatTag(
    title: String,
    color: ColorCode
) {
  def format: String = s"$color$title"
}
