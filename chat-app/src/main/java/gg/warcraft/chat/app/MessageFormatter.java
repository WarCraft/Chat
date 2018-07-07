package gg.warcraft.chat.app;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.profile.ChatProfile;

public class MessageFormatter {
    private final String formattingString;

    public MessageFormatter(String formattingString) {
        this.formattingString = formattingString;
    }

    public String format(Channel channel, ChatProfile sender, String text) {
        return channel.getColor() + formattingString
                .replaceAll("<channel\\.name>", channel.getName())
                .replaceAll("<channel\\.color>", channel.getColor().toString())
                .replaceAll("<sender\\.name>", sender.getName())
                .replaceAll("<sender\\.tag>", sender.getTag().getName())
                .replaceAll("<sender\\.color>", sender.getTag().getColor().toString())
                .replaceAll("<text>", text);
    }
}
