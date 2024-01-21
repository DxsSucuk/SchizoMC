package de.presti.schizomc.actions;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface ISchizoAction {

    void onTrigger(Player player, List<Map.Entry<Player, Float>> players);
    default void onTick(long tick, List<Map.Entry<Player, Float>> players) {}

    String name();
    int minPlayers();
    float triggerSanity();
    float triggerChance();

}
