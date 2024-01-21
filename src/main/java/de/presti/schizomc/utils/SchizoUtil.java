package de.presti.schizomc.utils;

import com.github.juliarn.npclib.api.Npc;
import com.github.juliarn.npclib.api.Position;
import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil;
import de.presti.schizomc.SchizoMC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import static org.bukkit.util.NumberConversions.square;


public class SchizoUtil {

    public static String schizoPrefix = "AWDADW--!!";

    public static boolean locationNotVisible(Vector eyeLocationDirection, Vector eyeLocation, Vector blockLocation) {

        float angle = (float) Math.toDegrees(eyeLocationDirection
                .angle(blockLocation.subtract(eyeLocation)));

        /*SchizoMC.getInstance().getLogger().info("");
        SchizoMC.getInstance().getLogger().info("Angle: " + angle);
        SchizoMC.getInstance().getLogger().info("Location: " + blockLocation);
        SchizoMC.getInstance().getLogger().info("");*/

        return angle > 80;
    }

    public static Vector convertPositionToVector(Position position) {
        return new Vector(position.x(), position.y(), position.z());
    }

    public static Sound[] getPossibleSounds(Player player) {
        World world = player.getWorld();
        if (world.getEnvironment() == World.Environment.NETHER) {
            return new Sound[]{Sound.ENTITY_GHAST_SCREAM, Sound.ENTITY_GHAST_WARN};
        } else if (world.getEnvironment() == World.Environment.THE_END) {
            return new Sound[]{Sound.ENTITY_ENDER_DRAGON_GROWL, Sound.ENTITY_ENDER_DRAGON_GROWL};
        } else {
            return Arrays.stream(Sound.values()).filter(x -> x.name().contains("ZOMBIE") || x.name().contains("SKELETON") || x.name().contains("CREEPER") ||
                    x.name().contains("WOLF") || x.name().contains("SPIDER") || x.name().contains("ENDERMAN") || x.name().contains("WITCH") || x.name().contains("VILLAGER") ||
                    x.name().contains("SILVERFISH") || x.name().contains("SLIME") || (x.name().contains("PIG") && x.name().contains("PIGLIN"))).toArray(Sound[]::new);
        }
    }


    public static float getSanity(Player player) {
        if (ArrayUtils.ignorePlayers.contains(player.getUniqueId())) return 1.0F;

        return ArrayUtils.schizoPlayers.getOrDefault(player.getUniqueId(), 1.0F);
    }

    public static void updateSanity(Player player, float sanity) {
        if (ArrayUtils.ignorePlayers.contains(player.getUniqueId())) return;

        if (sanity < 0F) sanity = 0;
        if (sanity > 1F) sanity = 1;

        float previousSanity = ArrayUtils.schizoPlayers.getOrDefault(player.getUniqueId(), 1.0F);

        if (sanity <= 0.75F && !(previousSanity <= 0.75)) {
            broadcastMessage("§c" + player.getName() + " is starting to lose their mind! 75% sanity.", ServerOperator::isOp);
        } else if (sanity <= 0.65F && !(previousSanity <= 0.65)) {
            broadcastMessage("§c" + player.getName() + " is starting to lose their mind! 65% sanity.", ServerOperator::isOp);
        } else if (sanity <= 0.55F && !(previousSanity <= 0.55)) {
            broadcastMessage("§c" + player.getName() + " is starting to lose their mind! 55% sanity.", ServerOperator::isOp);
        } else if (sanity <= 0.35F && !(previousSanity <= 0.35)) {
            broadcastMessage("§c" + player.getName() + " is starting to lose their mind! 35% sanity.", ServerOperator::isOp);
        } else if (sanity <= 0.15F && !(previousSanity <= 0.15)) {
            broadcastMessage("§c" + player.getName() + " is starting to lose their mind! 15% sanity.", ServerOperator::isOp);
        }

        ArrayUtils.schizoPlayers.put(player.getUniqueId(), sanity);

        float finalSanity = sanity;
        SchizoMC.getInstance().getSchizoManager().getSchizoByTriggerSanity(sanity).forEach(schizo -> {
            if (schizo.minPlayers() <= Bukkit.getOnlinePlayers().size()) {
                if (schizo.triggerChance() * getTriggerScale(finalSanity) >= ThreadLocalRandom.current().nextFloat()) {
                    schizo.onTrigger(player, ArrayUtils.getSchizoPlayers().entrySet().stream().filter(playerFloatEntry -> playerFloatEntry.getKey().isOnline()).toList());
                }
            }
        });
    }

    public static void broadcastMessage(String message, Predicate<Player> predicate) {
        Bukkit.getOnlinePlayers().stream().filter(predicate).forEach(player -> {
            player.sendMessage(message);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1F);
        });
    }

    private static long retry = 0;

    public static Sound getRandomSound(Player player) {
        Sound[] sounds = getPossibleSounds(player);

        return sounds[ThreadLocalRandom.current().nextInt(sounds.length)];
    }

    public static Player getRandomSchizo(List<Map.Entry<Player, Float>> players, Predicate<Player> predicate) {
        if (players.isEmpty()) return null;

        Player player = players.get(ThreadLocalRandom.current().nextInt(players.size())).getKey();
        if (predicate.test(player) && players.size() > 1 && retry < 3) {
            retry++;
            return getRandomSchizo(players, predicate);
        }

        retry = 0;
        return player;
    }

    public static void runActionViaRunnable(Consumer<Void> x) {
        Bukkit.getScheduler().runTask(SchizoMC.getInstance(), () -> x.accept(null));
    }

    public static List<Block> getBlocks(Location pos1, Location pos2)
    {
        if(pos1.getWorld() != pos2.getWorld())
            return null;

        World world = pos1.getWorld();
        List<Block> blocks = new ArrayList<>();

        int x1 = pos1.getBlockX();
        int y1 = pos1.getBlockY();
        int z1 = pos1.getBlockZ();

        int x2 = pos2.getBlockX();
        int y2 = pos2.getBlockY();
        int z2 = pos2.getBlockZ();


        int lowestX = Math.min(x1, x2);
        int lowestY = Math.min(y1, y2);
        int lowestZ = Math.min(z1, z2);

        int highestX = Math.max(x1, x2);
        int highestY = Math.max(y1, y2);
        int highestZ = Math.max(z1, z2);

        for(int x = lowestX; x <= highestX; x++) {
            for (int y = lowestY; y <= highestY; y++) {
                for (int z = lowestZ; z <= highestZ; z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static Location getBlockBehindPlayer(Player player) {
        return getBlockBehindPlayer(player, new Vector(0, 0, 0));
    }

    public static Material[] getBlockMats() {
        return Arrays.stream(Material.values()).filter(x -> x.isBlock() && x.isSolid()).toArray(Material[]::new);
    }

    public static Location getBlockBehindPlayer(Player player, Vector addition) {
        Vector inverseDirectionVec = addition.multiply(player.getLocation().getDirection().normalize().multiply(-1));
        return player.getLocation().add(inverseDirectionVec);
    }

    public static double distance(Npc<?, ?, ?, ?> npc, Location location) {
        return Math.sqrt(BukkitPlatformUtil.distance(npc, location));
    }

    public static float getTriggerScale(float sanity) {
        if (sanity <= 0.3) {
            return 1.5f;
        } else if (sanity <= 0.5) {
            return 1.25f;
        } else if (sanity <= 0.75) {
            return 1.1f;
        } else {
            return 1.0f;
        }
    }
}
