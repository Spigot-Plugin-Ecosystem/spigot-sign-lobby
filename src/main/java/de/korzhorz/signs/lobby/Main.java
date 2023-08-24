package de.korzhorz.signs.lobby;

import de.korzhorz.signs.lobby.handlers.MySQLHandler;
import de.korzhorz.signs.lobby.util.ColorTranslator;
import de.korzhorz.signs.lobby.util.GitHubUpdater;
import de.korzhorz.signs.lobby.configs.ConfigFiles;
import de.korzhorz.signs.lobby.handlers.BungeeCordHandler;
import de.korzhorz.signs.lobby.util.SignDatabase;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static BungeeCordHandler bungeeCordHandler = new BungeeCordHandler();

    @Override
    public void onEnable() {
        final String consolePrefix = "&7[&6Signs&7]&r ";

        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&7Enabling"));
        
        this.getDataFolder().mkdir();

        // Plugin channels
        this.getServer().getConsoleSender().sendMessage(ColorTranslator.translate(consolePrefix + "&7Setting up plugin channels"));
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", Main.bungeeCordHandler);
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
    }

    @Override
    public void onDisable() {
        MySQLHandler.disconnect();
    }
    
    public void loadCommands() {

    }
    
    public void loadEvents() {

    }
}
