package de.korzhorz.signs.lobby.listeners;

import de.korzhorz.signs.lobby.configs.ConfigFiles;
import de.korzhorz.signs.lobby.handlers.BungeeCordHandler;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class EVT_PlayerInteractEvent implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if(block == null) {
            return;
        }

        if(!(block.getState() instanceof Sign)) {
            return;
        }

        List<String> signs = ConfigFiles.signs.getStringList("signs");
        for(String sign : signs) {
            String[] signData = sign.split(":");

            if(!(
                signData[1].equals(block.getWorld().getName()) &&
                signData[2].equals(String.valueOf(block.getX())) &&
                signData[3].equals(String.valueOf(block.getY())) &&
                signData[4].equals(String.valueOf(block.getZ()))
            )) {
                continue;
            }

            String serverName = signData[0];
            BungeeCordHandler.getInstance().sendPluginMessage(player, "Connect", new String[]{serverName});
        }

    }
}
