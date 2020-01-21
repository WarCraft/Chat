package gg.warcraft.chat.channel

import gg.warcraft.monolith.api.core.command.CommandHandler
import gg.warcraft.monolith.api.util.ColorCode

trait Channel extends CommandHandler {
  val name: String
  val aliases: Set[String]
  val shortcut: Option[String]
  val color: ColorCode
  val formatString: String
}
