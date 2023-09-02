package de.korzhorz.signs.lobby.configs;

import de.korzhorz.signs.lobby.Main;
import de.korzhorz.signs.lobby.PluginConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConfigFiles {
    public static ConfigFile config = new ConfigFile("config.yml");
    public static ConfigFile messages = new ConfigFile("messages.yml");
    public static ConfigFile updater = new ConfigFile("updater.yml");
    public static ConfigFile signs = new ConfigFile("signs.yml");

    public static void initFileContents() {
        // Config
        if(PluginConfig.mySql) {
            config.setDefault("mysql.host", "localhost");
            config.setDefault("mysql.port", 3306);
            config.setDefault("mysql.database", "database");
            config.setDefault("mysql.username", "username");
            config.setDefault("mysql.password", "password");
        }
        if(PluginConfig.requireBungeeCord) {
            config.setDefault("bungeecord.enforce", false);
        }

        config.setDefault("signs.update-interval", 5000);
        config.setDefault("signs.background-blocks", true);

        config.setDefault("signs.info.ready.0", "%servername%");
        config.setDefault("signs.info.ready.1", "&r[&2&lBetreten&r]");
        config.setDefault("signs.info.ready.2", "&r%motd%");
        config.setDefault("signs.info.ready.3", "&6%onlineplayers%&r / &6&l%maxplayers%");
        config.setDefault("signs.info.ready.background-block", "LIME_CONCRETE");

        config.setDefault("signs.info.full.0", "%servername%");
        config.setDefault("signs.info.full.1", "&r[&6&lBetreten&r]");
        config.setDefault("signs.info.full.2", "&r%motd%");
        config.setDefault("signs.info.full.3", "&6%onlineplayers%&r / &6&l%maxplayers%");
        config.setDefault("signs.info.full.background-block", "YELLOW_CONCRETE");

        config.setDefault("signs.info.maintenance.0", "%servername%");
        config.setDefault("signs.info.maintenance.1", "&r[&4&lWartungen&r]");
        config.setDefault("signs.info.maintenance.2", " ");
        config.setDefault("signs.info.maintenance.3", "&6--&r / &6&l--");
        config.setDefault("signs.info.maintenance.background-block", "RED_CONCRETE");

        config.setDefault("signs.info.offline.0", "%servername%");
        config.setDefault("signs.info.offline.1", "&r[&4&lOffline&r]");
        config.setDefault("signs.info.offline.2", " ");
        config.setDefault("signs.info.offline.3", "&6--&r / &6&l--");
        config.setDefault("signs.info.offline.background-block", "RED_CONCRETE");

        config.save();

        // Messages
        messages.setDefault("prefix", "&6&l" + PluginConfig.pluginName + " &8»");

        messages.setDefault("commands.errors.no-player", "&cDu musst ein Spieler sein um diesen Befehl auszuführen.");
        messages.setDefault("commands.errors.no-permission", "&cDu hast keine Rechte um diesen Befehl auszuführen.");
        messages.setDefault("commands.errors.bad-usage", "&cBenutze: &7%usage%");
        messages.setDefault("commands.errors.save-failed", "&cDie Änderungen konnten nicht gespeichert werden.");
        messages.setDefault("commands.errors.invalid-server-name", "&cUngültiger Server-Name.");
        messages.setDefault("commands.errors.no-sign", "&cDu musst auf ein Schild schauen, wenn du diesen Befehl ausführst.");

        messages.setDefault("commands.set-sign.success", "&aDas Schild wurde erfolgreich mit dem Server synchronisiert.");

        messages.save();

        // Updater
        updater.setDefault("latest", JavaPlugin.getPlugin(Main.class).getDescription().getVersion());
        updater.setDefault("last-checked", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        updater.save();

        // Signs
        List<String> signData = new ArrayList<>();
        signs.setDefault("signs", signData);
        signs.save();
    }
}
