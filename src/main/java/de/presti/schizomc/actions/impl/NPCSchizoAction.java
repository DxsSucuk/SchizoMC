package de.presti.schizomc.actions.impl;

import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil;
import de.presti.schizomc.actions.ISchizoAction;
import de.presti.schizomc.actions.SchizoActionAnnotation;
import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;


@SchizoActionAnnotation(name = "NPC", minPlayers = 2, triggerSanity = 0.2F, triggerChance = 0.001F)
public class NPCSchizoAction implements ISchizoAction {

    @Override
    public void onTrigger(Player player) {

    }

    @Override
    public void onTick(int tick, List<Map.Entry<Player, Float>> players) {
        if (tick % 10 != 0) return;

        List<Player> players = ArrayUtils.schizoPlayers.entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline() && playerFloatEntry.getValue() >= 0.36).map(Map.Entry::getKey).toList();

        ArrayUtils.npcs.forEach(x -> {
            if (players.stream().noneMatch(p -> BukkitPlatformUtil.distance(x, p.getLocation()) <= 45)) {
                SchizoUtil.broadcastMessage("§cDeleting NPC §7" + x.profile().uniqueId() + ", because of distance§c!", ServerOperator::isOp);
                x.unlink();
            }

            if (players.stream().anyMatch(p -> BukkitPlatformUtil.distance(x, p.getLocation()) <= 45 &&
                    !SchizoUtil.locationNotVisible(p.getEyeLocation().getDirection(), p.getEyeLocation().toVector(),
                            new Vector(x.position().blockX(), x.position().blockY(), x.position().blockZ())))) {
                SchizoUtil.broadcastMessage("§cDeleting NPC §7" + x.profile().uniqueId() + ", because of visibility§c!", ServerOperator::isOp);
                x.unlink();
            }
        });
    }
}
