package de.presti.schizomc.actions.impl;

import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil;
import de.presti.schizomc.actions.ISchizoAction;
import de.presti.schizomc.actions.SchizoActionAnnotation;
import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.NPCUtil;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


@SchizoActionAnnotation(name = "NPC", minPlayers = 2, triggerSanity = 0.2F, triggerChance = 0.001F)
public class NPCSchizoAction implements ISchizoAction {

    @Override
    public void onTrigger(Player player, List<Map.Entry<Player, Float>> players) {
        List<Player> possibleNPCs = new ArrayList<>(Bukkit.getOnlinePlayers());
        possibleNPCs.removeIf(x -> x.getUniqueId() == player.getUniqueId());

        Player npcPlayer = possibleNPCs.get(ThreadLocalRandom.current().nextInt(possibleNPCs.size()));

        SchizoUtil.broadcastMessage("§cFaked NPC for " + player.getName() + " that is " + npcPlayer.getName() + "!", ServerOperator::isOp);

        ArrayUtils.npcs.add(NPCUtil.createNPC(npcPlayer.getUniqueId(), SchizoUtil.getBlockBehindPlayer(player, Vector.getRandom().multiply(5)),
                player.getEyeLocation(), npcPlayer, players.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() <= 0.35f).map(Map.Entry::getKey).toList()));
    }

    @Override
    public void onTick(int tick, List<Map.Entry<Player, Float>> originalPlayers) {
        if (tick % 10 != 0) return;

        List<Player> players = originalPlayers.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() >= 0.36).map(Map.Entry::getKey).toList();

        ArrayUtils.npcs.forEach(x -> {
            if (players.stream().noneMatch(p -> BukkitPlatformUtil.distance(x, p.getLocation()) <= 45)) {
                SchizoUtil.broadcastMessage("§cDeleting NPC §7" + x.profile().uniqueId() + "§c, because of distance!", ServerOperator::isOp);
                x.unlink();
            }

            if (players.stream().anyMatch(p -> BukkitPlatformUtil.distance(x, p.getLocation()) <= 45 &&
                    !SchizoUtil.locationNotVisible(p.getEyeLocation().getDirection(), p.getEyeLocation().toVector(),
                            SchizoUtil.convertPositionToVector(x.position())))) {
                SchizoUtil.broadcastMessage("§cDeleting NPC §7" + x.profile().uniqueId() + "§c, because of visibility!", ServerOperator::isOp);
                x.unlink();
            }
        });
    }
}
