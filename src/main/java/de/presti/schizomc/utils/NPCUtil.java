package de.presti.schizomc.utils;

import com.github.juliarn.npclib.api.Npc;
import com.github.juliarn.npclib.api.Platform;
import com.github.juliarn.npclib.api.profile.Profile;
import com.github.juliarn.npclib.api.profile.ProfileResolver;
import com.github.juliarn.npclib.api.protocol.enums.ItemSlot;
import com.github.juliarn.npclib.bukkit.BukkitPlatform;
import com.github.juliarn.npclib.bukkit.BukkitWorldAccessor;
import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil;
import com.github.juliarn.npclib.common.CommonNpcTracker;
import com.github.juliarn.npclib.common.npc.CommonNpcBuilder;
import de.presti.schizomc.SchizoMC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCUtil {

    public static Platform<World, Player, ItemStack, Plugin> platform = BukkitPlatform.bukkitNpcPlatformBuilder()
            .extension(SchizoMC.getInstance())
            .debug(true)
            .worldAccessor(BukkitWorldAccessor.nameBasedAccessor())
            .npcTracker(CommonNpcTracker.newNpcTracker())
            .build();

    public static Npc createNPC(String name, Location loc, Location lookat, ItemStack item, Player player) {
        ArrayList<Player> list = new ArrayList<>();
        list.add(player);
        return createNPC(name, loc, lookat, item, list);
    }

    public static Npc createNPC(String name, Location loc, Location lookat, ItemStack item, List<Player> viewers) {
        UUID uuid = UUID.fromString(PlayerInfo.getUUID(name));
        return createNPC(name, uuid, loc, lookat, item, viewers);
    }

    public static Npc createNPC(UUID uuid, Location loc, Location lookat, ItemStack item, List<Player> viewers) {
        String name = PlayerInfo.getName(uuid.toString());
        return createNPC(name, uuid, loc, lookat, item, viewers);
    }

    public static Npc createNPC(String name, UUID uuid, Location loc, Location lookat, ItemStack item, List<Player> viewers) {
        try {
            return createNPC(ProfileResolver.mojang().resolveProfile(Profile.unresolved(uuid)).get(), loc, lookat, item, viewers);
        } catch (Exception ignore) {
            return createNPC(Profile.resolved(name, uuid), loc, lookat, item, viewers);
        }
    }

    public static Npc createNPC(Profile.Resolved profile, Location loc, Location lookat, ItemStack item, List<Player> viewers) {

        if (profile == null) {
            SchizoMC.getInstance().getLogger().info("No Profile given!");
            return null;
        }

        try {
            Npc.Builder commonNpcBuilder = new CommonNpcBuilder<>(platform)
                    .position(BukkitPlatformUtil.positionFromBukkitLegacy(loc))
                    .profile(profile.withName("§7"));

            Npc npc = commonNpcBuilder.buildAndTrack();

            if (lookat != null)
                npc.lookAt(BukkitPlatformUtil.positionFromBukkitLegacy(lookat));

            for (Player player : viewers) {
                if (!npc.trackedPlayers().contains(player))
                    npc.trackPlayer(player);
            }

            if (item != null)
                npc.changeItem(ItemSlot.MAIN_HAND, item);

            return npc;
        } catch (Exception exception) {
            SchizoMC.getInstance().getLogger().warning("Received an error in NPCUtil: " + exception.getMessage());
        }

        return null;
    }

}