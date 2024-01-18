package de.presti.schizomc.actions;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface ISchizoAction {

    void onTrigger(Player player, List<Map.Entry<Player, Float>> players);
    default void onTick(int tick, List<Map.Entry<Player, Float>> players) {}

}
