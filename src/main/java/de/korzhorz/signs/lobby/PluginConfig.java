package de.korzhorz.signs.lobby;

import de.korzhorz.signs.lobby.commands.CMD_SetSign;
import de.korzhorz.signs.lobby.database.DB_Signs;
import de.korzhorz.signs.lobby.listeners.EVT_PlayerInteractEvent;
import de.korzhorz.signs.lobby.listeners.pluginchannels.PC_SignsUpdate;
import de.korzhorz.signs.lobby.util.bungeecord.PluginChannelEvent;
import de.korzhorz.signs.lobby.util.data.Command;
import de.korzhorz.signs.lobby.util.database.DatabaseTableUtil;
import org.bukkit.event.Listener;

public class PluginConfig {
    public static String pluginName = "Signs-Lobby";

    public static boolean requireBungeeCord = true;
    public static boolean pluginChannels = true;
    public static boolean mySql = true;
    public static boolean requireMySql = true;

    public static PluginChannelEvent[] pluginChannelEvents = new PluginChannelEvent[]{
        new PC_SignsUpdate()
    };
    public static DatabaseTableUtil[] databaseTableUtils = new DatabaseTableUtil[]{
        DB_Signs.getInstance()
    };
    public static Command[] commands = new Command[]{
        new Command("setsign", new CMD_SetSign())
    };
    public static Listener[] listeners = new Listener[]{
        new EVT_PlayerInteractEvent()
    };

    public static String gitHubUser = "Spigot-Plugin-Ecosystem";
    public static String gitHubRepo = "spigot-signs-lobby";

    private PluginConfig() {

    }
}
