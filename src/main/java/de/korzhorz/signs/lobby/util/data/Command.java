package de.korzhorz.signs.lobby.util.data;

import org.bukkit.command.CommandExecutor;

public record Command(String name, CommandExecutor commandExecutor) {
}
