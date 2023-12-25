package de.presti.schizomc.commands;

import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Sanity implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (sender instanceof Player player) {
            if (args.length != 0 && player.isOp()) {
                if (args[0].equalsIgnoreCase("off")) {
                    ArrayUtils.schizoPlayers.put(player, 1F);
                    ArrayUtils.ignorePlayers.add(player);
                    player.sendMessage("§cYour schizophrenia has been disabled!");
                    return true;
                } else if (args[0].equalsIgnoreCase("fuck")) {
                    SchizoUtil.updateSanity(player, SchizoUtil.getSanity(player) / 2);
                    player.sendMessage("§cYour sanity has been fucked!");
                    return true;
                } else if (args[0].equalsIgnoreCase("all")) {
                    ArrayUtils.schizoPlayers.forEach((t, sanity) ->
                            player.sendMessage("§c" + t.getName() + ": §4" + String.format("%,.2f", sanity) + "%§c!"));
                }

                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    player.sendMessage("§cThis player is not online!");
                    return true;
                }

                player.sendMessage("§c" + target.getName() + "'s sanity is at §4" + String.format("%,.2f", ArrayUtils.schizoPlayers.getOrDefault(target, 1.0F)) + "%§c!");
            } else {
                player.sendMessage("§cYour sanity is at §4" + String.format("%,.2f", ArrayUtils.schizoPlayers.getOrDefault(player, 1.0F)) + "%§c!");
            }
        } else {
            sender.sendMessage("You must be a player to execute this command!");
        }
        return true;
    }
}
