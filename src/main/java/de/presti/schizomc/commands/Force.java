package de.presti.schizomc.commands;

import de.presti.schizomc.SchizoMC;
import de.presti.schizomc.actions.ISchizoAction;
import de.presti.schizomc.utils.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Force implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (sender.isOp()) {
            if (args.length == 2) {
                Player t  = Bukkit.getPlayer(args[0]);

                if (t == null) {
                    sender.sendMessage("§cThis player is not online!");
                    return true;
                }

                String action = args[1];
                ISchizoAction actionClass = SchizoMC.getInstance().getSchizoManager().getSchizoByName(action);

                if (actionClass == null) {
                    sender.sendMessage("§cThis action does not exist!");
                    return true;
                }

                actionClass.onTrigger(t, ArrayUtils.getSchizoPlayers().entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline()).toList());
                sender.sendMessage("§cTriggered " + action + " for " + t.getName() + "!");
            }
        }
        return false;
    }
}
