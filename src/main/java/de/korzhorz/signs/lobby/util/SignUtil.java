package de.korzhorz.signs.lobby.util;

import de.korzhorz.signs.lobby.configs.ConfigFiles;
import de.korzhorz.signs.lobby.data.ServerData;
import de.korzhorz.signs.lobby.util.messages.CTUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.HangingSign;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

import java.util.ArrayList;
import java.util.List;

public class SignUtil {
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

        ServerData serverData = ServerData.getUpdatedServerData(serverName);
        SignUtil.updateSigns(serverName, serverData);
    }

    public static void updateSigns(String serverName, ServerData serverData) {
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

            if(serverData == null) {
                serverData = new ServerData(serverName, "", 0, 0, false, true);
            }

            SignUtil.updateSign(signBlock, serverData);
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

            SignUtil.updateSign(signBlock, serverData);
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

    private static void updateSign(Sign sign, ServerData serverData) {
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
            line = CTUtil.translate(line);
            if(!(line.equals(""))) {
                frontSide.setLine(i, line);
                backSide.setLine(i, line);
            }
        }

        sign.update();

        if(!(ConfigFiles.config.getBoolean("signs.background-blocks"))) {
            return;
        }

        if(sign instanceof HangingSign) {
            return;
        }

        BlockData blockData = sign.getBlockData();
        if(!(blockData instanceof Directional directional)) {
            return;
        }

        Block backgroundBlock = sign.getBlock().getRelative(directional.getFacing().getOppositeFace());
        Material material = Material.getMaterial(ConfigFiles.config.getString(configPath + "background-block"));
        assert material != null;
        backgroundBlock.setType(material);
    }
}
