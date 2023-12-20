package de.presti.schizomc.utils;

import org.bukkit.util.Vector;


public class SchizoUtil {

    public static String schizoPrefix = "SCHIZO--!!";

    public static boolean locationNotVisible(Vector eyeLocationDirection, Vector eyeLocation, Vector blockLocation) {
        float angle = eyeLocationDirection
                .angle(blockLocation.subtract(eyeLocation));

        return angle > 0.3F;
    }

}
