package gg.warcraft.chat.api.profile;

import gg.warcraft.monolith.api.util.ColorCode;

/**
 * A ChatTag is a string that is optionally prepended to a {@code Message} of a {@code Player}. It can be used to
 * display server ranks and the like.
 */
public interface ChatTag {

    /**
     * @return The name of this chat tag. Never null or empty.
     */
    String getName();

    /**
     * @return The color of this chat tag. Never null.
     */
    ColorCode getColor();
}
