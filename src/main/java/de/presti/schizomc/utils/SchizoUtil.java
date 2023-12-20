package de.presti.schizomc.utils;

import de.presti.schizomc.SchizoMC;
import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;


public class SchizoUtil {

    public static String schizoPrefix = "SCHIZO--!!";

    public static boolean locationNotVisible(Vector eyeLocationDirection, Vector eyeLocation, Vector blockLocation) {
        float angle = eyeLocationDirection
                .angle(blockLocation.subtract(eyeLocation));

        return angle > 0.3F;
    }

    public static void runActionViaRunnable(Consumer<Void> x) {
        Bukkit.getScheduler().runTask(SchizoMC.getInstance(), () -> x.accept(null));
    }
}
