package xyz.mobmaker.fairenchants;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static xyz.mobmaker.fairenchants.FairEnchants.costSaving;

public class ExtractCommand implements CommandExecutor, TabCompleter {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be run by players.");
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();
        Enchantment toExtract = Enchantment.getByKey(NamespacedKey.fromString(args[0]));
        if (toExtract == null) {
            sender.sendMessage("You need to specify a valid enchant. (use the tab-complete!)");
            return true;
        }

        if (toExtract.getKey().getKey().toLowerCase().contains("curse")) {
            sender.sendMessage("You can't extract curses.");
            return true;
        }

        if (!heldItem.containsEnchantment(toExtract)) {
            sender.sendMessage("The item in your main hand does not have the enchant you specified.");
            sender.sendMessage("No action has been taken.");
            return true;

        }

        //Determine the cost, or retrieve it if it exists already
        int cost;
        Map<String, Integer> costs = new HashMap<>();
        String keyString = toExtract.getKey().toString();
        int level = heldItem.getEnchantmentLevel(toExtract);
        PersistentDataContainer pdc = heldItem.getItemMeta().getPersistentDataContainer();

        //check PDC for cost map
        if (pdc.has(costSaving, PersistentDataType.BYTE_ARRAY)) {
            costs = Utils.rebuildCosts(pdc.get(costSaving, PersistentDataType.BYTE_ARRAY));
        }

        if (costs.containsKey(keyString)) {
            cost = costs.get(keyString);
        } else {
            int randomCost = (int) (Math.random() * 5.0) + 12;
            cost = Utils.experienceCalculator(toExtract, level) * randomCost;
        }

        //experience cost
        int playerExp = Utils.getPlayerExp(player);
        if (playerExp < cost) {
            costs.put(keyString, cost);
            pdc.set(costSaving, PersistentDataType.BYTE_ARRAY, Utils.saveCosts(costs));
            sender.sendMessage("You don't have enough XP for this.");
            return true;
        }

        Utils.changePlayerExp(player, -cost);

        //create enchanted book
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) book.getItemMeta();
        bookMeta.addStoredEnchant(toExtract, level, false);
        book.setItemMeta(bookMeta);

        heldItem.removeEnchantment(toExtract);
        sender.sendMessage(Component.text("Successfully extracted ")
                .append(toExtract.displayName(level))
                .append(Component.text(" from your item.")));
        //give the book to player
        int emptySlot = inventory.firstEmpty();
        if (emptySlot < 0) {
            player.getWorld().dropItemNaturally(player.getLocation(), book);
        } else {
            inventory.setItem(emptySlot, book);
        }

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("extract") && args.length == 1) {
            return Utils.enchants;
        }
        return null;
    }
}
