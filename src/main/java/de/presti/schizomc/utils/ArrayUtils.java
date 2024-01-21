package de.presti.schizomc.utils;

import com.github.juliarn.npclib.api.Npc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ArrayUtils {

    public static List<UUID> ignorePlayers = new ArrayList<>();
    public static Map<UUID, Float> schizoPlayers = new HashMap<>();

    public static Map<Player, Float> getSchizoPlayers() {
        Map<Player, Float> map = new HashMap<>();

        schizoPlayers.forEach((uuid, aFloat) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                map.put(player, aFloat);
            }
        });

        return map;
    }

    public static Map<Player, List<Location>> schizoPlayerBlocks = new HashMap<>();
    public static List<String> schizoMessages = new ArrayList<>();
    public static List<Location> schizoBlocks = new ArrayList<>();

    public static List<Npc> npcs = new ArrayList<>();

}
