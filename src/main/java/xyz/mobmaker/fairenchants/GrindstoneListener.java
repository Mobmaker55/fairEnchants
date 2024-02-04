package xyz.mobmaker.fairenchants;

import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GrindstoneListener implements Listener {

    @EventHandler
    public void onGrindstoneComplete(InventoryClickEvent e) {
        Inventory clickInventory = e.getClickedInventory();
        if (!(clickInventory instanceof GrindstoneInventory)) {
            return;
        }
        if (e.getRawSlot() != 2) {
            return;
        }
        ItemStack disenchant = ((GrindstoneInventory) clickInventory).getUpperItem();
        if (disenchant == null) {
            disenchant = ((GrindstoneInventory) clickInventory).getLowerItem();
        }
        if (disenchant == null) {
            return;
        }
        Map<Enchantment, Integer> enchantments = disenchant.getEnchantments();
        HumanEntity player = e.getWhoClicked();
        for (Enchantment ench : enchantments.keySet()) {
            int experience = Utils.experienceCalculator(ench, enchantments.get(ench));
            World world = player.getWorld();
            ExperienceOrb orb = world.spawn(player.getLocation(), ExperienceOrb.class);
            orb.setExperience(experience * 30);


        }


    }
}
