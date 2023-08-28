package de.korzhorz.signs.lobby;

import de.korzhorz.signs.lobby.commands.CMD_SetSign;
import de.korzhorz.signs.lobby.data.ServerData;
import de.korzhorz.signs.lobby.handlers.MySQLHandler;
import de.korzhorz.signs.lobby.handlers.ServerDataHandler;
import de.korzhorz.signs.lobby.handlers.SignHandler;
import de.korzhorz.signs.lobby.listeners.EVT_PlayerInteractEvent;
import de.korzhorz.signs.lobby.util.ColorTranslator;
import de.korzhorz.signs.lobby.util.GitHubUpdater;
import de.korzhorz.signs.lobby.configs.ConfigFiles;
import de.korzhorz.signs.lobby.handlers.BungeeCordHandler;
import de.korzhorz.signs.lobby.util.SignDatabase;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        final String consolePrefix = "&7[&6Signs&7]&r ";

        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&7Enabling"));
        
        this.getDataFolder().mkdir();

        // Plugin channels
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&7Setting up plugin channels"));
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", BungeeCordHandler.getInstance());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&aPlugin channels set up"));
        
        // Configuration files
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&7Loading files"));
        ConfigFiles.initFileContents();
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&aFiles loaded"));

        // MySQL database
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&7Connecting to MySQL database"));
        if(MySQLHandler.connect()) {
            // Create database tables
            SignDatabase signDatabase = new SignDatabase();
            signDatabase.createTables();
            this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&aConnected to MySQL database"));
        } else {
            this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&cCouldn't connect to MySQL database"));
            this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&cDisabling plugin"));
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Commands
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&7Loading commands"));
        loadCommands();
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&aCommands loaded"));
        
        // Events
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&7Loading events"));
        loadEvents();
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&aEvents loaded"));
        
        // Update checker
        if(GitHubUpdater.updateAvailable()) {
            this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix));
            this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&9A new update for this plugin is available"));
            this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix));
        }
        
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&aPlugin enabled &7- Version: &6v" + this.getDescription().getVersion()));
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&aDeveloped by &6KorzHorz"));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                List<ServerData> updatedServerData = ServerDataHandler.getUpdatedServerData();
                if(updatedServerData.isEmpty()) {
                    return;
                }

                SignHandler.updateSigns(updatedServerData);
            }
        }, (int) (((double) ConfigFiles.config.getInt("signs.update-interval")) / 1000 * 20L), (int) (((double) ConfigFiles.config.getInt("signs.update-interval")) / 1000 * 20L));
    }

    @Override
    public void onDisable() {
        MySQLHandler.disconnect();
    }
    
    public void loadCommands() {
        Objects.requireNonNull(this.getCommand("setsign")).setExecutor(new CMD_SetSign());
    }
    
    public void loadEvents() {
        Bukkit.getPluginManager().registerEvents(new EVT_PlayerInteractEvent(), this);
    }
}
