package de.presti.schizomc.actions.impl;

import de.presti.schizomc.actions.ISchizoAction;
import de.presti.schizomc.utils.SchizoUtil;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SoundSchizoAction implements ISchizoAction {
    @Override
    public void onTrigger(Player player, List<Map.Entry<Player, Float>> players) {
        SchizoUtil.broadcastMessage("§cSound for " + player.getName() + "!", ServerOperator::isOp);
        player.playSound(SchizoUtil.getBlockBehindPlayer(player, Vector.getRandom().multiply(5)), SchizoUtil.getRandomSound(player), 0.35F, ThreadLocalRandom.current().nextFloat());
    }

    @Override
    public String name() {
        return "Sound";
    }

    @Override
    public int minPlayers() {
        return 1;
    }

    @Override
    public float triggerSanity() {
        return 0.5F;
    }

    @Override
    public float triggerChance() {
        return 0.01F;
    }
}
