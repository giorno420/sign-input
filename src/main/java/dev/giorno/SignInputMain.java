package dev.giorno;

import org.bukkit.plugin.java.JavaPlugin;

public final class SignInputMain extends JavaPlugin {

    public static SignInputMain instance;
    public SignManager signManager;

    @Override
    public void onEnable() {
        instance = this;
        signManager = new SignManager(this);
        signManager.init();
        getCommand("signgui").setExecutor(new ShowSignCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
