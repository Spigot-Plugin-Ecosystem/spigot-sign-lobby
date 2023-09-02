package de.korzhorz.signs.lobby;

import de.korzhorz.signs.lobby.configs.ConfigFiles;
import de.korzhorz.signs.lobby.data.ServerData;
import de.korzhorz.signs.lobby.util.SignUtil;
import de.korzhorz.signs.lobby.util.bungeecord.PluginChannelEvent;
import de.korzhorz.signs.lobby.util.bungeecord.PluginChannelInitiator;
import de.korzhorz.signs.lobby.util.bungeecord.PluginChannelUtil;
import de.korzhorz.signs.lobby.util.data.Command;
import de.korzhorz.signs.lobby.util.database.DatabaseTableUtil;
import de.korzhorz.signs.lobby.util.database.MySQLUtil;
import de.korzhorz.signs.lobby.util.messages.CTUtil;
import de.korzhorz.signs.lobby.util.meta.Data;
import de.korzhorz.signs.lobby.util.meta.GitHubUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        final String consolePrefix = "&7[&6" + PluginConfig.pluginName + "&7]&r ";
        final ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();

        consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&7Enabling"));

        this.getDataFolder().mkdir();

        // Configuration files
        consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&7Loading files"));
        ConfigFiles.initFileContents();
        consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&aFiles loaded"));

        // Check if server is running in BungeeCord network
        if(this.detectBungeeCord()) {
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&cWARNING: The BungeeCord mode was forced to be enabled"));
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&cWARNING: This could cause problems if the server is not actually running in a BungeeCord network"));
        }
        if(PluginConfig.requireBungeeCord && !(Data.bungeeCord)) {
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&cServer is not running in a BungeeCord network"));
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&cIf this is a mistake, please set the option \"bungeecord.enforce\" in the config.yml to \"true\""));
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&cDisabling plugin"));
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Plugin channels
        if(PluginConfig.pluginChannels) {
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&7Setting up plugin channels"));
            this.loadPluginChannels();
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&aPlugin channels set up"));
        }

        // MySQL database
        if(PluginConfig.mySql) {
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&7Connecting to MySQL database"));
            Data.mySql = MySQLUtil.connect();

            if(Data.mySql) {
                this.loadDatabase();
                consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&aConnected to MySQL database"));
            } else {
                consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&cCouldn't connect to MySQL database"));

                if(PluginConfig.requireMySql) {
                    consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&cDisabling plugin"));
                    Bukkit.getServer().getPluginManager().disablePlugin(this);
                    return;
                }
            }
        }

        // Commands
        consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&7Loading commands"));
        this.loadCommands();
        consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&aCommands loaded"));
        
        // Events
        consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&7Loading events"));
        this.loadEvents();
        consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&aEvents loaded"));
        
        // Update checker
        if(GitHubUpdater.updateAvailable()) {
            consoleSender.sendMessage(CTUtil.translate(consolePrefix));
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&9A new update for this plugin is available"));
            consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&9You can download it at " + GitHubUpdater.getGitHubUrl()));
            consoleSender.sendMessage(CTUtil.translate(consolePrefix));
        }

        consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&aPlugin enabled &7- Version: &6v" + this.getDescription().getVersion()));
        consoleSender.sendMessage(CTUtil.translate(consolePrefix + "&aDeveloped by &6KorzHorz"));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                List<ServerData> updatedServerData = ServerData.getUpdatedServerData();
                if(updatedServerData.isEmpty()) {
                    return;
                }

                SignUtil.updateSigns(updatedServerData);
            }
        }, (int) (((double) ConfigFiles.config.getInt("signs.update-interval")) / 1000 * 20L), (int) (((double) ConfigFiles.config.getInt("signs.update-interval")) / 1000 * 20L));
    }

    @Override
    public void onDisable() {
        MySQLUtil.disconnect();
    }

    public boolean detectBungeeCord() {
        try {
            Data.bungeeCord = Bukkit.getServer().spigot().getConfig().getBoolean("settings.bungeecord");
        } catch(Exception e) {
            // Catch block left empty on purpose
        }

        if(!(Data.bungeeCord) && ConfigFiles.config.getBoolean("bungeecord.enforce")) {
            Data.bungeeCord = true;
            return true;
        }

        return false;
    }

    public void loadPluginChannels() {
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", PluginChannelUtil.getInstance());
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.loadPluginMessages();
    }

    public void loadPluginMessages() {
        for(PluginChannelEvent pluginChannelEvent : PluginConfig.pluginChannelEvents) {
            PluginChannelInitiator.registerPluginChannelEvent(pluginChannelEvent.getHandledSubChannel(), pluginChannelEvent);
        }
    }

    public void loadDatabase() {
        for(DatabaseTableUtil databaseTableUtil : PluginConfig.databaseTableUtils) {
            databaseTableUtil.createTable();
        }
    }
    
    public void loadCommands() {
        for(Command command : PluginConfig.commands) {
            PluginCommand pluginCommand = this.getCommand(command.name());
            if(pluginCommand == null) {
                continue;
            }

            pluginCommand.setExecutor(command.commandExecutor());
        }
    }
    
    public void loadEvents() {
        for(Listener listener : PluginConfig.listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}
