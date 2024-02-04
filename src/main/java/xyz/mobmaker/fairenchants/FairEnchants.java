package xyz.mobmaker.fairenchants;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class FairEnchants extends JavaPlugin {

    public static NamespacedKey costSaving;
    @Override
    public void onEnable() {
        costSaving = new NamespacedKey(this, "extractCost");
        this.getServer().getPluginManager().registerEvents(new GrindstoneListener(), this);
        this.getCommand("extract").setExecutor(new ExtractCommand());
        Utils.generateEnchants();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
