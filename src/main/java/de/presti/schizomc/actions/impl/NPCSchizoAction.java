package de.presti.schizomc.actions.impl;

import com.github.juliarn.npclib.api.Npc;
import de.presti.schizomc.SchizoMC;
import de.presti.schizomc.actions.ISchizoAction;
import de.presti.schizomc.utils.ArrayUtils;
import de.presti.schizomc.utils.LocationUtil;
import de.presti.schizomc.utils.NPCUtil;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class NPCSchizoAction implements ISchizoAction {

    @Override
    public String name() {
        return "NPC";
    }

    @Override
    public int minPlayers() {
        return 2;
    }

    @Override
    public float triggerSanity() {
        return 0.2F;
    }

    @Override
    public float triggerChance() {
        return 0.001F;
    }

    @Override
    public void onTrigger(Player player, List<Map.Entry<Player, Float>> players) {
        List<Player> possibleNPCs = new ArrayList<>(Bukkit.getOnlinePlayers());
        possibleNPCs.removeIf(x -> x.getUniqueId() == player.getUniqueId());

        Player npcPlayer = possibleNPCs.get(ThreadLocalRandom.current().nextInt(possibleNPCs.size()));

        SchizoUtil.broadcastMessage("§cFaked NPC for " + player.getName() + " that is " + npcPlayer.getName() + "!", ServerOperator::isOp);
        List<Player> viewPlayers = players.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() <= 0.35f).map(Map.Entry::getKey).toList();

        ArrayUtils.npcs.add(NPCUtil.createNPC(npcPlayer.getUniqueId(), LocationUtil.getLocFromRadWithGround(player.getLocation(), 20, 5, 20),
                player.getEyeLocation(), npcPlayer, viewPlayers));
    }

    @Override
    public void onTick(long tick, List<Map.Entry<Player, Float>> originalPlayers) {
        if (tick % 10 != 0) return;

        List<Player> players = originalPlayers.stream().filter(playerFloatEntry -> playerFloatEntry.getValue() >= 0.36).map(Map.Entry::getKey).toList();


        List<Npc> toDelete = new ArrayList<>();

        ArrayUtils.npcs.forEach(x -> {
            /*SchizoMC.getInstance().getLogger().info("Checking NPC " + x.profile().uniqueId() + "!");
            SchizoMC.getInstance().getLogger().info("Position: " + String.format("x: %s, y: %s, z: %s", x.position().x(), x.position().y(), x.position().z()));
            SchizoMC.getInstance().getLogger().info("Distance: " + players.stream().map(p -> SchizoUtil.distance(x, p.getLocation())).toList());*/

            if (originalPlayers.stream().map(Map.Entry::getKey).noneMatch(p -> SchizoUtil.distance(x, p.getLocation()) <= 45)) {
                SchizoUtil.broadcastMessage("§cDeleting NPC §7" + x.profile().uniqueId() + "§c, because of distance!", ServerOperator::isOp);
                x.unlink();
                toDelete.add(x);
            }

            if (players.stream().anyMatch(p -> SchizoUtil.distance(x, p.getLocation()) <= 45 &&
                    !SchizoUtil.locationNotVisible(p.getEyeLocation().getDirection(), p.getEyeLocation().toVector(),
                            SchizoUtil.convertPositionToVector(x.position())))) {
                SchizoUtil.broadcastMessage("§cDeleting NPC §7" + x.profile().uniqueId() + "§c, because of visibility!", ServerOperator::isOp);
                x.unlink();
                toDelete.add(x);
            }
        });

        ArrayUtils.npcs.removeAll(toDelete);
    }
}
