package de.presti.schizomc.actions.impl;

import de.presti.schizomc.SchizoMC;
import de.presti.schizomc.actions.ISchizoAction;
import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BlockSchizoAction implements ISchizoAction {
    @Override
    public void onTrigger(Player player, List<Map.Entry<Player, Float>> players) {
        SchizoUtil.broadcastMessage("§cSchizo Block for " + player.getName() + "!", ServerOperator::isOp);
        Location playerLocation = player.getLocation().subtract(0, -1, 0);

        int x, y, z;
        int x2, y2, z2;

        x = playerLocation.getBlockX() - 10;
        y = playerLocation.getBlockY() - 3;
        z = playerLocation.getBlockZ() - 10;

        x2 = playerLocation.getBlockX() + 10;
        y2 = playerLocation.getBlockY() + 3;
        z2 = playerLocation.getBlockZ() + 10;

        List<Block> blocks = SchizoUtil.getBlocks(new Location(playerLocation.getWorld(), x, y, z), new Location(playerLocation.getWorld(), x2, y2, z2));

        SchizoMC.getInstance().getLogger().info("Blocks: " + blocks);

        if (blocks != null) {
            blocks.removeIf(b -> b.getType() == Material.AIR);

            blocks.forEach(b ->
                    player.sendBlockChange(b.getLocation(),
                            SchizoUtil.getBlockMats()[ThreadLocalRandom.current().nextInt(SchizoUtil.getBlockMats().length)].createBlockData()));
            ArrayUtils.schizoPlayerBlocks.put(player, blocks.stream().map(Block::getLocation).toList());
        }
    }

    @Override
    public void onTick(long tick, List<Map.Entry<Player, Float>> players) {
        ISchizoAction.super.onTick(tick, players);

        if (tick % 20 != 0) return;
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
    }

    @Override
    public String name() {
        return "Block";
    }

    @Override
    public int minPlayers() {
        return 2;
    }

    @Override
    public float triggerSanity() {
        return 0.84F;
    }

    @Override
    public float triggerChance() {
        return 0.003f;
    }
}
