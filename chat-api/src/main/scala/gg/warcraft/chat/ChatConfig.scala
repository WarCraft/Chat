package gg.warcraft.chat

import gg.warcraft.chat.channel.{GlobalChannel, LocalChannel}

case class ChatConfig(
    defaultChannel: String,
    defaultTag: String,
    globalChannels: List[GlobalChannel],
    localChannels: List[LocalChannel]
)
