package de.korzhorz.signs.lobby.commands;

import de.korzhorz.signs.lobby.configs.Messages;
import de.korzhorz.signs.lobby.handlers.SignHandler;
import de.korzhorz.signs.lobby.util.ColorTranslator;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMD_SetSign implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ColorTranslator.translate(Messages.get("prefix") + "&r " + Messages.get("commands.errors.no-player")));
            return true;
        }

        Player player = (Player) sender;

        if(!(player.hasPermission("signs.setup"))) {
            player.sendMessage(ColorTranslator.translate(Messages.get("prefix") + "&r " + Messages.get("commands.errors.no-permission")));
            return true;
        }

        if(args.length != 1) {
            String message = Messages.get("commands.errors.bad-usage");
            message = message.replaceAll("%usage%", command.getUsage());
            player.sendMessage(ColorTranslator.translate(Messages.get("prefix") + "&r " + message));
            return true;
        }

        String serverName = args[0];
        if(serverName.contains(":")) {
            String message = Messages.get("commands.errors.invalid-server-name");
            player.sendMessage(ColorTranslator.translate(Messages.get("prefix") + "&r " + message));
            return true;
        }

        Block block = player.getTargetBlockExact(5);
        if(block == null || !(block.getState() instanceof Sign sign)) {
            String message = Messages.get("commands.errors.no-sign");
            player.sendMessage(ColorTranslator.translate(Messages.get("prefix") + "&r " + message));
            return true;
        }

        SignHandler.initSign(sign, serverName);

        player.sendMessage(ColorTranslator.translate(Messages.get("prefix") + "&r " + Messages.get("commands.set-sign.success")));

        return true;
    }
}
