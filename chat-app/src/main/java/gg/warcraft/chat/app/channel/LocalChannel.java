package gg.warcraft.chat.app.channel;

import com.google.common.base.MoreObjects;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.List;

public class LocalChannel implements Channel {
    private final String name;
    private final List<String> aliases;
    private final String shortcut;
    private final ColorCode color;
    private final String formattingString;
    private final float radius;

    public LocalChannel(String name, List<String> aliases, String shortcut, ColorCode color, String formattingString,
                        float radius) {
        this.name = name;
        this.aliases = aliases;
        this.shortcut = shortcut;
        this.color = color;
        this.formattingString = formattingString;
        this.radius = radius;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public String getShortcut() {
        return shortcut;
    }

    @Override
    public ColorCode getColor() {
        return color;
    }

    @Override
    public String getFormattingString() {
        return formattingString;
    }

    public float getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("name", name)
                .add("aliases", aliases)
                .add("shortcut", shortcut)
                .add("color", color)
                .add("formattingString", formattingString)
                .add("radius", radius)
                .toString();
    }
}
