package gg.warcraft.chat.app.logger;

import gg.warcraft.chat.api.message.Message;

public class NoopMessageLogger implements MessageLogger {

    @Override
    public void setGroup(String group) {
        // do nothing
    }

    @Override
    public void log(Message message) {
        // do nothing
    }
}
