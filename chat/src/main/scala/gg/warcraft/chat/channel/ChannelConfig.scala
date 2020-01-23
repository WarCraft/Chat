package gg.warcraft.chat.channel

case class ChannelConfig(
    globalChannels: List[GlobalChannel],
    localChannels: List[LocalChannel],
    defaultChannel: String
)
