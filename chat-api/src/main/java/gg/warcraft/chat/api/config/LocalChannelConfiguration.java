package gg.warcraft.chat.api.config;

import gg.warcraft.monolith.api.util.ColorCode;

import java.util.List;

public interface LocalChannelConfiguration {

    String getName();

    List<String> getAliases();

    String getShortcut();

    ColorCode getColor();

    String getFormattingString();

    float getRadius();
}
