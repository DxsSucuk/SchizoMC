package de.presti.schizomc.events;

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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class SchizoEvents implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if (ArrayUtils.schizoPlayers.containsKey(e.getPlayer())) {
            ArrayUtils.schizoBlocks.add(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onExplosionEvent(ExplosionPrimeEvent e) {
        AtomicBoolean shouldCancel = new AtomicBoolean(true);

        Location entityLocation = e.getEntity().getLocation();
        Collection<Player> players = entityLocation.getNearbyPlayers(25);

        for (Player player : players) {
            if (ArrayUtils.schizoPlayers.containsKey(player)) {
                if (!SchizoUtil.locationNotVisible(player.getEyeLocation().getDirection(),
                        player.getEyeLocation().toVector(),
                        entityLocation.toVector()) &&
                        ArrayUtils.schizoPlayers.get(player) >= 0.75) {
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
        if (!e.isAsynchronous() || e.isCancelled() || !ArrayUtils.schizoPlayers.containsKey(e.getPlayer())) return;

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
