package gg.warcraft.chat.app.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gg.warcraft.chat.api.config.GlobalChannelConfiguration;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.List;

public class SimpleGlobalChannelConfiguration implements GlobalChannelConfiguration {
    private final String name;
    private final List<String> aliases;
    private final String shortcut;
    private final ColorCode color;
    private final String formattingString;
    private final String requiredPermission;

    @JsonCreator
    public SimpleGlobalChannelConfiguration(@JsonProperty("name") String name,
                                            @JsonProperty("aliases") List<String> aliases,
                                            @JsonProperty("shortcut") String shortcut,
                                            @JsonProperty("color") ColorCode color,
                                            @JsonProperty("formattingString") String formattingString,
                                            @JsonProperty("requiredPermission") String requiredPermission) {
        this.name = name;
        this.aliases = aliases;
        this.shortcut = shortcut;
        this.color = color;
        this.formattingString = formattingString;
        this.requiredPermission = requiredPermission;
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
    public String getRequiredPermission() {
        return requiredPermission;
    }
}
