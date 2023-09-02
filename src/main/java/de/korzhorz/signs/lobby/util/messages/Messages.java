package de.korzhorz.signs.lobby.util.messages;

import de.korzhorz.signs.lobby.configs.ConfigFiles;

public class Messages {
    private Messages() {

    }

    public static String get(String path) {
        return ConfigFiles.messages.getString(path);
    }
}
