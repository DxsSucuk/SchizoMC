package de.presti.schizomc.utils;

import com.google.gson.JsonObject;
import de.presti.schizomc.SchizoMC;

public class PlayerInfo {

    public static String link = "https://playerdb.co/api/player/minecraft/";

    public static String getUUID(String name) {

        JsonObject j = RequestUtility.getJSON(link + name).getAsJsonObject();

        if (j.has("data")) {
            try {
                JsonObject datas = j.getAsJsonObject("data");

                JsonObject player = datas.getAsJsonObject("player");

                return player.get("id").getAsString();
            } catch (Exception e) {
                SchizoMC.getInstance().getLogger().warning("Error while getting UUID from " + name + "\nException: " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public static String getName(String uuid) {
        JsonObject j = RequestUtility.getJSON(link + uuid).getAsJsonObject();

        if (j.has("data")) {
            try {
                JsonObject datas = j.getAsJsonObject("data");

                JsonObject player = datas.getAsJsonObject("player");

                return player.get("username").getAsString();
            } catch (Exception e) {
                SchizoMC.getInstance().getLogger().warning("Error while getting Name from " + uuid + "\nException: " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

}