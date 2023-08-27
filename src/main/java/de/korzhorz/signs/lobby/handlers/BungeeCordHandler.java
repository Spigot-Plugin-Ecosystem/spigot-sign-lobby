package de.korzhorz.signs.lobby.handlers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.korzhorz.signs.lobby.Main;
import de.korzhorz.signs.lobby.data.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BungeeCordHandler implements PluginMessageListener {
    private static final BungeeCordHandler instance = new BungeeCordHandler();

    private BungeeCordHandler() {}

    public static BungeeCordHandler getInstance() {
        return instance;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if(!(channel.equals("BungeeCord"))) {
            return;
        }

        ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(message);
        String subChannel = byteArrayDataInput.readUTF();

        if(subChannel.equals("signs:update")) {
            String serverName = byteArrayDataInput.readUTF();

            // Retrieve changed server data and update the signs
            ServerData serverData = ServerDataHandler.getUpdatedServerData(serverName);
            SignHandler.updateSigns(serverName, serverData);
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
