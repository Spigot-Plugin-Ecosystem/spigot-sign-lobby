package de.korzhorz.signs.lobby.util;

import java.util.HashMap;

public class ServerData {
    public static HashMap<String, ServerData> serverData = new HashMap<String, ServerData>();

    private final String name;
    private final String motd;
    private final int maxPlayers;
    private final int onlinePlayers;

    public ServerData(String name, String motd, int maxPlayers, int onlinePlayers) {
        this.name = name;
        this.motd = motd;
        this.maxPlayers = maxPlayers;
        this.onlinePlayers = onlinePlayers;
    }

    public String getName() {
        return this.name;
    }

    public String getMotd() {
        return this.motd;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getOnlinePlayers() {
        return this.onlinePlayers;
    }
}
