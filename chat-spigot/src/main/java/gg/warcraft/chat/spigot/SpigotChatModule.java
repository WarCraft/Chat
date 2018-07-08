package gg.warcraft.chat.spigot;

import gg.warcraft.chat.api.service.ChatServerAdapter;
import gg.warcraft.chat.app.AbstractChatModule;

public class SpigotChatModule extends AbstractChatModule {

    public SpigotChatModule(String messageLoggerType) {
        super(messageLoggerType);
    }

    @Override
    public void configure() {
        super.configure();
        bind(ChatServerAdapter.class).to(SpigotChatAdapter.class);
    }
}
