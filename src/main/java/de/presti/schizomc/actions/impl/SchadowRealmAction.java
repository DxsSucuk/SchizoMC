package de.presti.schizomc.actions.impl;

import com.github.juliarn.npclib.api.Npc;
import com.github.juliarn.npclib.api.Position;
import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil;
import de.presti.schizomc.SchizoMC;
import de.presti.schizomc.actions.ISchizoAction;
import de.presti.schizomc.utils.LocationUtil;
import de.presti.schizomc.utils.NPCUtil;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class SchadowRealmAction implements ISchizoAction {
    @Override
    public void onTrigger(Player player, List<Map.Entry<Player, Float>> players) {
        SchizoUtil.broadcastMessage("§cShadow Realm for " + player.getName() + "!", ServerOperator::isOp);

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all == player) continue;

            player.hidePlayer(SchizoMC.getInstance(), all);
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 10, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 10, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 10, 10, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 10, 10, false, false));

        for (int x = 0; x < 30; x++) {
            Npc npc = NPCUtil.createNPC(SchizoMC.profile, LocationUtil.getLocFromRad(player.getLocation(), 20, 5, 20, ((new Random().nextInt(2)) == 0), false, ((new Random().nextInt(2)) == 0)),
                    player.getLocation(), null, null, List.of(player));

            if (npc == null) continue;

            new BukkitRunnable() {
                long ticksLeft = 180;

                @Override
                public void run() {
                    ticksLeft -= 20;
                    Position position = npc.position();

                    if (SchizoUtil.locationNotVisible(player.getEyeLocation().toVector(), player.getLocation().toVector(), new Vector(position.x(), position.y(), position.z()))) {
                        npc.lookAt(BukkitPlatformUtil.positionFromBukkitLegacy(player.getLocation()));
                    }

                    if (ticksLeft <= 0) {
                        npc.unlink();
                        cancel();
                    }
                }
            }.runTaskTimer(SchizoMC.getInstance(), 20, 20);
        }

        (new BukkitRunnable() {
            @Override
            public void run() {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (all == player) continue;

                    player.showPlayer(SchizoMC.getInstance(), all);
                }
            }
        }).runTaskLater(SchizoMC.getInstance(), 20 * 10);
    }

    @Override
    public void onTick(long tick, List<Map.Entry<Player, Float>> players) {
        ISchizoAction.super.onTick(tick, players);
    }

    @Override
    public String name() {
        return "Shadow_Realm";
    }

    @Override
    public int minPlayers() {
        return 2;
    }

    @Override
    public float triggerSanity() {
        return 0.4F;
    }

    @Override
    public float triggerChance() {
        return 0.001F;
    }
}
