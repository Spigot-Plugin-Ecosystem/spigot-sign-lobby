package de.korzhorz.signs.lobby.util;

import org.bukkit.ChatColor;

public class ColorTranslator {
    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
