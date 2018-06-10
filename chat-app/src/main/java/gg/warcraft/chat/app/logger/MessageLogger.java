package gg.warcraft.chat.app.logger;

import gg.warcraft.chat.api.message.Message;

public interface MessageLogger {
    void log(Message message);

    void setGroup(String group);
}
