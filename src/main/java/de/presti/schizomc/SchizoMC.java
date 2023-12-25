package de.presti.schizomc;

import de.presti.schizomc.commands.Sanity;
import de.presti.schizomc.events.SchizoEvents;
import de.presti.schizomc.tasks.Schizophrenia;
import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SchizoMC extends JavaPlugin {

    private static SchizoMC instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new SchizoEvents(), this);
        getCommand("sanity").setExecutor(new Sanity());
        new Schizophrenia().runTaskTimerAsynchronously(this, 10L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() < 2) return;

                List<Map.Entry<Player, Float>> players = ArrayUtils.schizoPlayers.entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline()).toList();

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

                                SchizoMC.getInstance().getLogger().info("Calling from Entity delete.");
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

                toDeleteEntity.forEach(z -> SchizoUtil.runActionViaRunnable(aVoid -> z.remove()));
            }
        }.runTaskTimer(this, 10L, 10L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SchizoMC getInstance() {
        return instance;
    }
}
