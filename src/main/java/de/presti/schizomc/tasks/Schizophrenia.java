package de.presti.schizomc.tasks;

import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

public class Schizophrenia extends BukkitRunnable {


    @Override
    public void run() {

        List<Map.Entry<Player, Float>> players = ArrayUtils.schizoPlayers.entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline()).toList();

        ArrayUtils.schizoBlocks.forEach(block -> {
            boolean shouldDelete = true;

            for (Player player : players.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() >= 0.85).map(Map.Entry::getKey).toList()) {
                if (block.getWorld().equals(player.getWorld())) {
                    if (block.distance(player.getLocation()) <= 25) {
                        if (!SchizoUtil.locationNotVisible(player.getEyeLocation().getDirection(), player.getEyeLocation().toVector(), block.toVector())) {
                            shouldDelete = false;
                            break;
                        }
                    }
                }
            }

            if (shouldDelete) {
                block.getBlock().setType(Material.AIR);
            }
        });
    }
}
