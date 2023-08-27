package de.korzhorz.signs.lobby.configs;

public class Messages {
    public static String get(String path) {
        return ConfigFiles.messages.getString(path);
    }
}
