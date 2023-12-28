package de.presti.schizomc.tasks;

import de.presti.schizomc.SchizoMC;
import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Schizophrenia extends BukkitRunnable {

    @Override
    public void run() {

        SchizoUtil.runActionViaRunnable(x -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ArrayUtils.ignorePlayers.contains(player)) continue;
                float schizo = SchizoUtil.getSanity(player);

                Collection<Player> nearby = player.getLocation().getNearbyPlayers(15);

                if (nearby.size() >= 2) {
                    schizo += 0.002F;
                } else {
                    schizo -= 0.002F;
                }

                SchizoUtil.updateSanity(player, schizo);
            }
        });

        List<Map.Entry<Player, Float>> players = ArrayUtils.schizoPlayers.entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline()).toList();

        if (players.isEmpty()) return;

        players.forEach(x -> {
            if (x.getValue() <= 0.55F) {
                if (ThreadLocalRandom.current().nextFloat(1F) <= 0.01) {
                    Player player = x.getKey();

                    player.playSound(SchizoUtil.getBlockBehindPlayer(player, Vector.getRandom().multiply(5)), SchizoUtil.getRandomSound(player), 1F, 1F);
                }
            }
        });

        if (!ArrayUtils.schizoMessages.isEmpty() && ThreadLocalRandom.current().nextFloat(1F) <= 0.10) {
            Player player = SchizoUtil.getRandomSchizo(players, x -> ArrayUtils.ignorePlayers.contains(x));

            String randomMessage = ArrayUtils.schizoMessages.get(ThreadLocalRandom.current().nextInt(ArrayUtils.schizoMessages.size()));

            if (player != null) {
                player.chat(SchizoUtil.schizoPrefix + randomMessage);
            }

            ArrayUtils.schizoMessages.remove(randomMessage);
        }

        ArrayUtils.schizoBlocks.forEach(block -> {
            boolean shouldDelete = ThreadLocalRandom.current().nextFloat(1F) <= 0.10;

            for (Player player : players.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() >= 0.85).map(Map.Entry::getKey).toList()) {
                if (block.getWorld().equals(player.getWorld())) {
                    if (block.distance(player.getLocation()) <= 25) {
                        SchizoMC.getInstance().getLogger().info("Calling from Schizo blocks");
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

        ArrayUtils.schizoBlocks.clear();

        List<Player> toDeleteSchizoPlayerBlocks = new ArrayList<>();

        ArrayUtils.schizoPlayerBlocks.forEach((player, locations) -> {
            boolean shouldDelete = true;

            for (Player player1 : players.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() >= 0.85).map(Map.Entry::getKey).toList()) {
                if (player.getWorld().equals(player1.getWorld())) {
                    if (player.getLocation().distance(player1.getLocation()) <= 25) {
                        SchizoMC.getInstance().getLogger().info("Calling from Schizo Player blocks");
                        if (!SchizoUtil.locationNotVisible(player1.getEyeLocation().getDirection(), player1.getEyeLocation().toVector(), player.getLocation().toVector())) {
                            shouldDelete = false;
                            break;
                        }
                    }
                }
            }

            if (shouldDelete) {
                toDeleteSchizoPlayerBlocks.add(player);
                SchizoUtil.runActionViaRunnable(aVoid ->
                        locations.forEach(location -> player.sendBlockChange(location, location.getBlock().getBlockData())));
            }
        });

        toDeleteSchizoPlayerBlocks.forEach(ArrayUtils.schizoPlayerBlocks::remove);

        if (ThreadLocalRandom.current().nextFloat(1F) <= 0.03) {
            Player player = SchizoUtil.getRandomSchizo(players, x -> ArrayUtils.schizoPlayerBlocks.containsKey(x));

            if (ArrayUtils.ignorePlayers.contains(player)) return;

            if (player != null) {
                Location playerLocation = player.getLocation().subtract(0, -1, 0);
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
    }
}
