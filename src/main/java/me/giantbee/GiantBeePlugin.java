package me.giantbee;

import org.bukkit.plugin.java.JavaPlugin;

public final class GiantBeePlugin extends JavaPlugin {

    private GiantBeeBoss boss;

    @Override
    public void onEnable() {

        boss = new GiantBeeBoss(this);

        getServer().getPluginManager().registerEvents(
                new GiantBeeListener(this, boss),
                this
        );

        GiantBeeCommand command = new GiantBeeCommand(this, boss);

        getCommand("giantbee").setExecutor(command);
        getCommand("giantbee").setTabCompleter(command);

        getLogger().info("GiantBee enabled!");
    }

    @Override
    public void onDisable() {

        boss.remove();

        getLogger().info("GiantBee disabled!");
    }

    public GiantBeeBoss getBoss() {
        return boss;
    }
}