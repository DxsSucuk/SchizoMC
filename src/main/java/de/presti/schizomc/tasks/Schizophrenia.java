package de.presti.schizomc.tasks;

import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Schizophrenia extends BukkitRunnable {

    @Override
    public void run() {

        List<Map.Entry<Player, Float>> players = ArrayUtils.schizoPlayers.entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline()).toList();

        if (players.isEmpty()) return;

        if (!ArrayUtils.schizoMessages.isEmpty() && ThreadLocalRandom.current().nextFloat(1F) <= 0.10) {
            Player player = SchizoUtil.getRandomSchizo(players, x -> false);

            if (player != null) {
                player.chat(SchizoUtil.schizoPrefix + ArrayUtils.schizoMessages.get(ThreadLocalRandom.current().nextInt(ArrayUtils.schizoMessages.size())));
            }
        }

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
                SchizoUtil.runActionViaRunnable(aVoid -> block.getBlock().setType(Material.AIR));
            }
        });

        ArrayUtils.schizoPlayerBlocks.forEach((player, locations) -> {
            boolean shouldDelete = true;

            for (Player player1 : players.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() >= 0.85).map(Map.Entry::getKey).toList()) {
                if (player.getWorld().equals(player1.getWorld())) {
                    if (player.getLocation().distance(player1.getLocation()) <= 25) {
                        if (!SchizoUtil.locationNotVisible(player1.getEyeLocation().getDirection(), player1.getEyeLocation().toVector(), player.getLocation().toVector())) {
                            shouldDelete = false;
                            break;
                        }
                    }
                }
            }

            if (shouldDelete) {
                SchizoUtil.runActionViaRunnable(aVoid -> {
                    locations.forEach(location -> player.sendBlockChange(location, location.getBlock().getBlockData()));
                    ArrayUtils.schizoPlayerBlocks.remove(player);
                });
            }
        });

        if (ThreadLocalRandom.current().nextFloat(1F) <= 0.10) {
            Player player = SchizoUtil.getRandomSchizo(players, x -> ArrayUtils.schizoPlayerBlocks.containsKey(x));

            if (player != null) {
                Location playerLocation = player.getLocation();
                List<Block> blocks = SchizoUtil.getBlocks(playerLocation.add(10, 10, 10), playerLocation.subtract(10, 10, 10));

                if (blocks != null) {
                    blocks.removeIf(x -> x.getType() == Material.AIR);

                    blocks.forEach(x ->
                            player.sendBlockChange(x.getLocation(),
                                    Material.values()[ThreadLocalRandom.current().nextInt(Material.values().length)],
                                    (byte) 0));
                    ArrayUtils.schizoPlayerBlocks.put(player, blocks.stream().map(Block::getLocation).toList());
                }
            }
        }

        ArrayUtils.schizoBlocks.clear();

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

        toDeleteEntity.forEach(x -> SchizoUtil.runActionViaRunnable(aVoid -> x.remove()));
    }
}
