package de.presti.schizomc.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayUtils {

    public static List<Player> ignorePlayers = new ArrayList<>();
    public static Map<Player, Float> schizoPlayers = new HashMap<>();
    public static Map<Player, List<Location>> schizoPlayerBlocks = new HashMap<>();
    public static List<String> schizoMessages = new ArrayList<>();
    public static List<Location> schizoBlocks = new ArrayList<>();

}
