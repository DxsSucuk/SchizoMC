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
                    SchizoUtil.updateSanity(player, 1F);
                    ArrayUtils.ignorePlayers.add(player.getUniqueId());
                    player.sendMessage("§cYour schizophrenia has been disabled!");
                    return true;
                } else if (args[0].equalsIgnoreCase("fuck")) {
                    if (args.length == 2) {
                        Player target = Bukkit.getPlayer(args[1]);

                        if (target == null) {
                            player.sendMessage("§cThis player is not online!");
                            return true;
                        }

                        SchizoUtil.updateSanity(target, SchizoUtil.getSanity(target) / 2);
                        player.sendMessage("§c" + target.getName() + "'s sanity is at §4" + String.format("%,.2f", SchizoUtil.getSanity(target)) + "%§c!");
                        return true;
                    } else {
                        SchizoUtil.updateSanity(player, SchizoUtil.getSanity(player) / 2);
                        player.sendMessage("§cYour sanity has been fucked!");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("all")) {
                    ArrayUtils.getSchizoPlayers().forEach((t, sanity) -> {
                        if (t.isOnline()) {
                            player.sendMessage("§c" + t.getName() + ": §4" + String.format("%,.2f", sanity * 100) + "%§c - §4" + String.format("%,.2f", SchizoUtil.getTriggerScale(sanity) * 100) + "%§c!");
                        }
                    });
                    return true;
                }

                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    player.sendMessage("§cThis player is not online!");
                    return true;
                }

                player.sendMessage("§c" + target.getName() + "'s sanity is at §4" + String.format("%,.2f", SchizoUtil.getSanity(target)) + "%§c - §4" + String.format("%,.2f", SchizoUtil.getTriggerScale(SchizoUtil.getSanity(target)) * 100) + "%§c!");
            }/* else {
                player.sendMessage("§cYour sanity is at §4" + String.format("%,.2f", SchizoUtil.getSanity(player)) + "%§c!");
            }*/
        } else {
            sender.sendMessage("You must be a player to execute this command!");
        }
        return true;
    }
}
