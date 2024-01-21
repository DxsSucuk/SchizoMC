package de.presti.schizomc.events;

import de.presti.schizomc.SchizoMC;
import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class SchizoEvents implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent playerMoveEvent) {
        Player player = playerMoveEvent.getPlayer();
        Location from = playerMoveEvent.getFrom();
        Location to = playerMoveEvent.getTo();

        if (to.getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ()) {
            if (ArrayUtils.schizoPlayers.containsKey(player.getUniqueId())) {
                float sanity = SchizoUtil.getSanity(player);
                if (to.getBlock().getLightLevel() < 5) {
                    if (sanity <= 0.6F && ThreadLocalRandom.current().nextFloat() <= 0.001) {
                        player.playSound(SchizoUtil.getBlockBehindPlayer(player),
                                Sound.AMBIENT_CAVE, 1F, 1F);
                    }

                    SchizoUtil.updateSanity(player, sanity - (ThreadLocalRandom.current().nextFloat() * 0.001F));
                }

                if ((to.getBlock().getLightLevel() > 9 && to.getWorld().isDayTime()) || to.getBlock().getLightFromBlocks() > 9) {
                    SchizoUtil.updateSanity(player, sanity + (ThreadLocalRandom.current().nextFloat() * 0.0001F));
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if (SchizoUtil.getSanity(e.getPlayer()) >= 0.7F) {
            return;
        }

        if (ArrayUtils.schizoPlayers.containsKey(e.getPlayer().getUniqueId())) {
            ArrayUtils.schizoBlocks.add(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onExplosionEvent(ExplosionPrimeEvent e) {
        AtomicBoolean shouldCancel = new AtomicBoolean(true);

        Location entityLocation = e.getEntity().getLocation();
        Collection<Player> players = entityLocation.getNearbyPlayers(25);

        for (Player player : players) {
            if (ArrayUtils.schizoPlayers.containsKey(player.getUniqueId())) {
                SchizoMC.getInstance().getLogger().info("Calling from Explosion");
                if (!SchizoUtil.locationNotVisible(player.getEyeLocation().getDirection(),
                        player.getEyeLocation().toVector(),
                        entityLocation.toVector()) &&
                        SchizoUtil.getSanity(player) >= 0.75) {
                    shouldCancel.set(false);
                    break;
                }
            } else {
                shouldCancel.set(false);
                break;
            }
        }

        if (shouldCancel.get()) {
            players.forEach(x -> x.playSound(SchizoUtil.getBlockBehindPlayer(x), Sound.ENTITY_GENERIC_EXPLODE, 1F, 1F));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled() || !ArrayUtils.schizoPlayers.containsKey(e.getPlayer().getUniqueId())) return;

        String message = e.getMessage();

        if (message.startsWith(SchizoUtil.schizoPrefix)) {
            e.setMessage(message.substring(SchizoUtil.schizoPrefix.length()));
            Set<Player> old = e.getRecipients();
            e.getRecipients().clear();

            old.forEach(x -> {
                if (x.getUniqueId() != e.getPlayer().getUniqueId() && ThreadLocalRandom.current().nextBoolean()) {
                    e.getRecipients().add(x);
                }
            });
        } else {
            if (!ArrayUtils.schizoMessages.contains(e.getMessage())) {
                ArrayUtils.schizoMessages.add(e.getMessage());
            }
        }
    }

}
