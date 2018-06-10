package gg.warcrat.chat.app.profile;

import gg.warcraft.monolith.api.chat.profile.ChatTag;
import gg.warcraft.monolith.api.util.ColorCode;

public class PlayerChatTag implements ChatTag {
    private final String name;
    private final ColorCode color;

    public PlayerChatTag(String name, ColorCode color) {
        this.name = name;
        this.color = color;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ColorCode getColor() {
        return color;
    }
}
