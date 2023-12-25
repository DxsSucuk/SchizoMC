package de.presti.schizomc;

import de.presti.schizomc.commands.Sanity;
import de.presti.schizomc.events.SchizoEvents;
import de.presti.schizomc.tasks.Schizophrenia;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SchizoMC extends JavaPlugin {

    private static SchizoMC instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(new SchizoEvents(), this);
        getCommand("sanity").setExecutor(new Sanity());
        new Schizophrenia().runTaskTimerAsynchronously(this, 10L, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SchizoMC getInstance() {
        return instance;
    }
}
