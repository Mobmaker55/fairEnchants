package xyz.mobmaker.fairenchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

public class Utils {

    public static List<String> enchants;

    public static int experienceCalculator(Enchantment enchantment, int level) {
        int random = (int)(Math.random() * 5.0d) + 5;
        int multiplier = random / enchantment.getMaxLevel();
        return level * multiplier;
    }

    public static void generateEnchants() {
        enchants = new ArrayList<>();
        for (Enchantment ench : Enchantment.values()) {
            enchants.add(ench.getKey().toString());
        }

    }

    public static int expForNextLevel(int level){
        if(level <= 15){
            return 2*level+7;
        } else if(level <= 30){
            return 5*level-38;
        } else {
            return 9*level-158;
        }
    }

    // Calculate total experience up to a level
    public static int expFromLevels(int level){
        if(level <= 16){
            return (int) (Math.pow(level,2) + 6*level);
        } else if(level <= 31){
            return (int) (2.5*Math.pow(level,2) - 40.5*level + 360.0);
        } else {
            return (int) (4.5*Math.pow(level,2) - 162.5*level + 2220.0);
        }
    }

    public static int getPlayerExp(Player player){
        int exp = 0;
        int level = player.getLevel();

        // Get the amount of XP in past levels
        exp += expFromLevels(level);

        // Get amount of XP towards next level
        exp += Math.round(expForNextLevel(level) * player.getExp());

        return exp;
    }

    public static int changePlayerExp(Player player, int exp){
        // Get player's current exp
        int currentExp = getPlayerExp(player);

        // Reset player's current exp to 0
        player.setExp(0);
        player.setLevel(0);

        // Give the player their exp back, with the difference
        int newExp = currentExp + exp;
        player.giveExp(newExp);

        // Return the player's new exp amount
        return newExp;
    }

    public static byte[] saveCosts(Map<String, Integer> costs) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] ret = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(costs);
            out.flush();
            ret = bos.toByteArray();
            bos.close();
        } catch (IOException e) {

        }
        return ret;
    }

    public static Map<String, Integer> rebuildCosts(byte[] costsRaw) {
        ByteArrayInputStream bis = new ByteArrayInputStream(costsRaw);
        Map<String, Integer> ret = null;
        try {
            ObjectInputStream in = new ObjectInputStream(bis);
            ret = (Map<String, Integer>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
        }
        return ret;
    }

}
