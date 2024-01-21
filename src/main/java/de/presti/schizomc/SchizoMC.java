package de.presti.schizomc;

import com.github.juliarn.npclib.api.profile.Profile;
import com.github.juliarn.npclib.api.profile.ProfileResolver;
import de.presti.schizomc.actions.SchizoManager;
import de.presti.schizomc.commands.Force;
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
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class SchizoMC extends JavaPlugin {

    private static SchizoMC instance;
    private SchizoManager schizoManager;


    public static Profile.Resolved profile;

    @Override
    public void onEnable() {
        instance = this;

        instance.schizoManager = new SchizoManager();
        try {
            profile = ProfileResolver.mojang().resolveProfile(Profile.unresolved(UUID.fromString("2a457f64-fb51-4b8a-94e8-eee53d9695a3"))).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new SchizoEvents(), this);
        getCommand("sanity").setExecutor(new Sanity());
        getCommand("force").setExecutor(new Force());
        new Schizophrenia().runTaskTimerAsynchronously(this, 10L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() < 2) return;

                List<Map.Entry<Player, Float>> players = ArrayUtils.getSchizoPlayers().entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline()).toList();

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

                    boolean shouldDelete = ThreadLocalRandom.current().nextFloat(1F) <= 0.05;

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


        (new BukkitRunnable() {

            long tick;

            @Override
            public void run() {
                tick++;
                getSchizoManager().getSchizoList().forEach(x -> {
                    x.onTick(tick, ArrayUtils.getSchizoPlayers().entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline()).toList());
                });
            }
        }).runTaskTimerAsynchronously(this, 10L, 1L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SchizoMC getInstance() {
        return instance;
    }

    public SchizoManager getSchizoManager() {
        return schizoManager;
    }
}
