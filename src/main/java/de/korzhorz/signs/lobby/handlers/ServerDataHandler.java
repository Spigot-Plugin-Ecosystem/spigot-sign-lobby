package de.korzhorz.signs.lobby.handlers;

import de.korzhorz.signs.lobby.data.ServerData;
import de.korzhorz.signs.lobby.util.SignDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerDataHandler {
    public static HashMap<String, ServerData> serverData = new HashMap<>();

    public static List<ServerData> getUpdatedServerData() {
        SignDatabase signDatabase = new SignDatabase();
        List<ServerData> newServerData = signDatabase.getServerData();
        List<ServerData> updatedServerData = new ArrayList<>();

        for(ServerData serverData : newServerData) {
            if(!(ServerDataHandler.serverData.containsKey(serverData.getName()))) {
                updatedServerData.add(serverData);
            } else {
                ServerData oldServerData = ServerDataHandler.serverData.get(serverData.getName());
                if(serverData.equals(oldServerData)) {
                    continue;
                }

                updatedServerData.add(serverData);
            }

            ServerDataHandler.serverData.put(serverData.getName(), serverData);
        }

        return updatedServerData;
    }

    public static ServerData getUpdatedServerData(String serverName) {
        SignDatabase signDatabase = new SignDatabase();
        ServerData serverData = signDatabase.getServerData(serverName);

        if(serverData == null) {
            return null;
        }

        ServerDataHandler.serverData.put(serverData.getName(), serverData);

        return serverData;
    }
}
