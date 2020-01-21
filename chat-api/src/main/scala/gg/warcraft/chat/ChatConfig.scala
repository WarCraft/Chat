package gg.warcraft.chat

import gg.warcraft.chat.channel.{GlobalChannel, LocalChannel}

case class ChatConfig(
    globalChannels: List[GlobalChannel],
    localChannels: List[LocalChannel],
    defaultChannel: String
)
