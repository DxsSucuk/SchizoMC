package de.presti.schizomc.tasks;

import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
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

        List<Entity> entities = players.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() <= 0.55).map(Map.Entry::getKey).toList().stream().flatMap(p -> p.getNearbyEntities(10, 10, 10).stream()).toList();

        List<Entity> toDeleteEntity = new ArrayList<>();

        entities.forEach(entity -> {
            if (entity instanceof Player) {
                return;
            }

            if (toDeleteEntity.contains(entity)) {
                return;
            }

            if (entity.isDead()) {
                return;
            }

            if (toDeleteEntity.stream().anyMatch(c -> c.getUniqueId() == entity.getUniqueId())) {
                return;
            }

            boolean shouldDelete = true;

            for (Player player : players.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() >= 0.85).map(Map.Entry::getKey).toList()) {
                if (entity.getWorld().equals(player.getWorld())) {
                    if (entity.getLocation().distance(player.getLocation()) <= 25) {
                        if (!SchizoUtil.locationNotVisible(player.getEyeLocation().getDirection(), player.getEyeLocation().toVector(), entity.getLocation().toVector())) {
                            shouldDelete = false;
                            break;
                        }
                    }
                }
            }

            if (shouldDelete) {
                toDeleteEntity.add(entity);
            }
        });

        toDeleteEntity.forEach(Entity::remove);
    }
}
