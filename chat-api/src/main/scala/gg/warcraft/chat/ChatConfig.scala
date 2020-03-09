package gg.warcraft.chat

import gg.warcraft.chat.channel.{GlobalChannel, LocalChannel}
import gg.warcraft.monolith.api.util.ColorCode

final case class GlobalChannelConfig(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode,
    format: String,
    permission: Option[String]
)

final case class LocalChannelConfig(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode,
    format: String,
    radius: Float
)

case class ChatConfig(
    globalChannels: List[GlobalChannel],
    localChannels: List[LocalChannel],
    defaultChannel: String,
    defaultTag: String
)
