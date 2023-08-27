package de.korzhorz.signs.lobby.handlers;

import de.korzhorz.signs.lobby.configs.ConfigFiles;
import de.korzhorz.signs.lobby.data.ServerData;
import de.korzhorz.signs.lobby.util.ColorTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

import java.util.ArrayList;
import java.util.List;

public class SignHandler {
    public static void initSign(Sign sign, String serverName) {
        Location location = sign.getLocation();
        List<String> signs = ConfigFiles.signs.getStringList("signs");
        signs.add(
                serverName + ":" +
                location.getWorld().getName() + ":" +
                location.getBlockX() + ":" +
                location.getBlockY() + ":" +
                location.getBlockZ()
        );
        ConfigFiles.signs.set("signs", signs);
        ConfigFiles.signs.save();

        ServerData serverData = ServerDataHandler.getUpdatedServerData(serverName);
        SignHandler.updateSigns(serverData);
    }

    public static void updateSigns(ServerData serverData) {
        String serverName = serverData.getName();
        List<String> signs = ConfigFiles.signs.getStringList("signs");
        List<String> outdatedSigns = new ArrayList<>();

        for(String sign : signs) {
            String[] signData = sign.split(":");
            if(!(signData[0].equals(serverName))) {
                continue;
            }

            Location location = new Location(
                    Bukkit.getWorld(signData[1]),
                    Integer.parseInt(signData[2]),
                    Integer.parseInt(signData[3]),
                    Integer.parseInt(signData[4])
            );

            Block block = location.getBlock();
            if(!(block.getState() instanceof Sign signBlock)) {
                outdatedSigns.add(sign);
                continue;
            }

            SignHandler.setSignData(signBlock, serverData);
        }

        // Delete outdated signs
        if(outdatedSigns.isEmpty()) {
            return;
        }

        for(String sign : outdatedSigns) {
            signs.remove(sign);
        }

        ConfigFiles.signs.set("signs", signs);
        ConfigFiles.signs.save();
    }

    public static void updateSigns(List<ServerData> serverDataList) {
        List<String> serverNames = serverDataList.stream().map(ServerData::getName).toList();
        List<String> signs = ConfigFiles.signs.getStringList("signs");
        List<String> outdatedSigns = new ArrayList<>();

        for(String sign : signs) {
            String[] signData = sign.split(":");
            if(!(serverNames.contains(signData[0]))) {
                continue;
            }

            ServerData serverData = serverDataList.stream().filter(data -> data.getName().equals(signData[0])).findFirst().orElse(null);
            if(serverData == null) {
                continue;
            }

            Location location = new Location(
                    Bukkit.getWorld(signData[1]),
                    Integer.parseInt(signData[2]),
                    Integer.parseInt(signData[3]),
                    Integer.parseInt(signData[4])
            );

            Block block = location.getBlock();
            if(!(block.getState() instanceof Sign signBlock)) {
                outdatedSigns.add(sign);
                continue;
            }

            SignHandler.setSignData(signBlock, serverData);
        }

        // Delete outdated signs
        if(outdatedSigns.isEmpty()) {
            return;
        }

        for(String sign : outdatedSigns) {
            signs.remove(sign);
        }

        ConfigFiles.signs.set("signs", signs);
        ConfigFiles.signs.save();
    }

    private static void setSignData(Sign sign, ServerData serverData) {
        SignSide frontSide = sign.getSide(Side.FRONT);
        SignSide backSide = sign.getSide(Side.BACK);

        String configPath = "signs.info.";
        if(serverData.getOnline()) {
            if(serverData.getMaintenance()) {
                configPath += "maintenance.";
            } else if(serverData.getOnlinePlayers() >= serverData.getMaxPlayers()) {
                configPath += "full.";
            } else {
                configPath += "ready.";
            }
        } else {
            configPath += "offline.";
        }

        for(int i = 0; i < 4; i++) {
            String line = ConfigFiles.config.getString(configPath + i);
            if(line == null) {
                continue;
            }

            line = line.replaceAll("%servername%", serverData.getName())
                    .replaceAll("%motd%", serverData.getMotd())
                    .replaceAll("%onlineplayers%", String.valueOf(serverData.getOnlinePlayers()))
                    .replaceAll("%maxplayers%", String.valueOf(serverData.getMaxPlayers()));
            line = ColorTranslator.translate(line);
            if(!(line.equals(""))) {
                frontSide.setLine(i, line);
                backSide.setLine(i, line);
            }
        }

        sign.update();
    }
}
