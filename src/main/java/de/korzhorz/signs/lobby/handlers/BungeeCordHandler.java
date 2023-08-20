package de.korzhorz.signs.lobby.handlers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.korzhorz.signs.lobby.Main;
import de.korzhorz.signs.lobby.util.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeCordHandler implements PluginMessageListener {
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!(channel.equals("BungeeCord"))) {
            return;
        }

        ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(message);
        String subChannel = byteArrayDataInput.readUTF();

        if(subChannel.equals("Forward")) {
            String plugin = byteArrayDataInput.readUTF();
            if(!(plugin.equals("Signs-ServerDataBroadcast"))) {
                return;
            }

            boolean futureServerState = byteArrayDataInput.readBoolean(); // true meaning that the server will be online, false meaning that it's going to be offline
            String serverName = byteArrayDataInput.readUTF();
            String serverMotd = byteArrayDataInput.readUTF();
            int serverMaxPlayers = byteArrayDataInput.readInt();
            int serverOnlinePlayers = byteArrayDataInput.readInt();
            ServerData serverData = new ServerData(serverName, serverMotd, serverMaxPlayers, serverOnlinePlayers);

            ServerData.serverData.put(serverName, serverData);

            if(!(futureServerState)) {
                ServerData.serverData.remove(serverName);
            }

            // TODO: Remove this debug output
            System.out.println("----------");
            System.out.println("Received server data for server " + serverName);
            System.out.println("Server is " + (futureServerState ? "online" : "offline"));
            System.out.println("Motd: " + serverMotd);
            System.out.println("Max players: " + serverMaxPlayers);
            System.out.println("Online players: " + serverOnlinePlayers);
            System.out.println("----------");
            System.out.println();

            // TODO: Update the signs
        }
    }

    public void sendPluginMessage(String subChannel, String[] message) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.write(subChannel.getBytes());
            for(String string : message) {
                dataOutputStream.write(string.getBytes());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().sendPluginMessage(JavaPlugin.getPlugin(Main.class), "BungeeCord", byteArrayOutputStream.toByteArray());
    }
}
