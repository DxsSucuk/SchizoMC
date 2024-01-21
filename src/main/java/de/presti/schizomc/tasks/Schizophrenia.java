package de.presti.schizomc.tasks;

import de.presti.schizomc.SchizoMC;
import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
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
                if (ArrayUtils.ignorePlayers.contains(player.getUniqueId())) continue;
                float schizo = SchizoUtil.getSanity(player);

                Collection<Player> nearby = player.getLocation().getNearbyPlayers(15).stream().filter(z -> z.getGameMode() != org.bukkit.GameMode.SPECTATOR).toList();

                if (nearby.size() >= 2) {
                    schizo += 0.002F;
                } else {
                    schizo -= 0.002F;
                }

                SchizoUtil.updateSanity(player, schizo);
            }
        });

        List<Map.Entry<Player, Float>> players = ArrayUtils.getSchizoPlayers().entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline()).toList();

        if (players.isEmpty()) return;

        /*if (!ArrayUtils.schizoMessages.isEmpty() && ThreadLocalRandom.current().nextFloat(1F) <= 0.10) {
            Player player = SchizoUtil.getRandomSchizo(players, x -> ArrayUtils.ignorePlayers.contains(x.getUniqueId()));

            String randomMessage = ArrayUtils.schizoMessages.get(ThreadLocalRandom.current().nextInt(ArrayUtils.schizoMessages.size()));

            if (player != null) {
                SchizoUtil.runActionViaRunnable(x -> player.chat(SchizoUtil.schizoPrefix + randomMessage));
                SchizoUtil.broadcastMessage("§cFake Messages send!", ServerOperator::isOp);
            }

            ArrayUtils.schizoMessages.remove(randomMessage);
        }*/

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
                SchizoUtil.broadcastMessage("§cDeleted block at " + block.toVector() + "!", ServerOperator::isOp);
                SchizoUtil.runActionViaRunnable(aVoid -> block.getBlock().setType(Material.AIR));
            }
        });

        ArrayUtils.schizoBlocks.clear();
    }
}
