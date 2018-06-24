package gg.warcraft.chat.app.config;

import gg.warcraft.chat.api.config.LocalChannelConfiguration;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.ArrayList;
import java.util.List;

public class SimpleLocalChannelConfiguration implements LocalChannelConfiguration {
    private final String name;
    private final List<String> aliases;
    private final String shortcut;
    private final ColorCode color;
    private final String formattingString;
    private final float radius;

    public SimpleLocalChannelConfiguration() {
        this.name = "";
        this.aliases = new ArrayList<>();
        this.shortcut = "";
        this.color = ColorCode.WHITE;
        this.formattingString = "";
        this.radius = 0;
    }

    public SimpleLocalChannelConfiguration(String name, List<String> aliases, String shortcut, ColorCode color,
                                           String formattingString, float radius) {
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

    @Override
    public float getRadius() {
        return radius;
    }
}
