package rip.orbit.hcteams.events.killtheking;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.util.object.ItemBuilder;

public class KingEvent {

    @Setter
    private static boolean started;
    @Getter
    @Setter
    private static boolean scoreboardInfo = true;
    @Getter
    @Setter
    private static Player focusedPlayer;
    @Getter
    @Setter
    private static String reward;
    @Setter
    private static long time;

    public static long getTime() {
        return time - System.currentTimeMillis();
    }

    public static void equipPlayer() {
        if (focusedPlayer == null || !focusedPlayer.isOnline()) {
            return;
        }
        PlayerInventory inventory = focusedPlayer.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);

        inventory.setHelmet(new ItemBuilder(Material.DIAMOND_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).name("&c&lKing Helmet").enchantment(Enchantment.DURABILITY, 10).build());
        inventory.setChestplate(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).name("&c&lKing ChestPlate").enchantment(Enchantment.DURABILITY, 10).build());
        inventory.setLeggings(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).name("&c&lKing Leggings").enchantment(Enchantment.DURABILITY, 10).build());
        inventory.setBoots(new ItemBuilder(Material.DIAMOND_BOOTS).enchantment(Enchantment.PROTECTION_FALL, 5).name("&c&lKing Boots").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).enchantment(Enchantment.DURABILITY, 10).build());

        for (int i = 0; i < 36; i++) {
            inventory.addItem(new ItemBuilder(Material.POTION).data(16421).build());
        }

        inventory.setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).name("&c&lKing Sword").enchantment(Enchantment.DAMAGE_ALL, 5).enchantment(Enchantment.FIRE_ASPECT, 2).lore("&7" + focusedPlayer.getName() + '\'' + (focusedPlayer.getName().endsWith("s") ? "" : "s") + " sword").build());
        inventory.setItem(1, new ItemStack(Material.ENDER_PEARL, 32));
        inventory.setItem(7, new ItemBuilder(Material.GOLDEN_APPLE).amount(32).name("&6&lGapples").build());
        inventory.setItem(8, new ItemBuilder(Material.STICK).name("&c&lKing Knockback").enchantment(Enchantment.KNOCKBACK, 8).build());

        focusedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        focusedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        focusedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
        focusedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));

        focusedPlayer.updateInventory();

    }

    public static boolean isStarted(boolean ignore) {
        if (ignore) {
            return started;
        }
        return started && getReward() != null && getFocusedPlayer() != null;
    }

    public static void clean() {
        setStarted(false);
        setReward(null);

        if (focusedPlayer != null && focusedPlayer.isOnline()) {
            focusedPlayer.getActivePotionEffects().forEach(effect -> focusedPlayer.removePotionEffect(effect.getType()));
            focusedPlayer.getInventory().clear();
            focusedPlayer.getInventory().setArmorContents(null);
            focusedPlayer.updateInventory();

            focusedPlayer.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            SpawnTagHandler.removeTag(focusedPlayer);
        }

        setFocusedPlayer(null);
        setTime(0L);
    }

    public static String[] getStartedAlert() {
        return new String[]{
            ChatColor.GRAY + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588" + " " + ChatColor.DARK_RED + "[Kill The King Event]",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588\u2588\u2588" + ChatColor.GRAY + "\u2588\u2588\u2588",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588" + " " + ChatColor.GOLD + "If you kill " + focusedPlayer.getName() + ',',
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588 " + ChatColor.GOLD + "you'll win the event!",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588",
            ChatColor.GRAY + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588"
        };
    }

    static String[] getWinnerAlert(String playerName) {
        return new String[]{
            ChatColor.GRAY + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588" + " " + ChatColor.DARK_RED + "[Kill The King Event]",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588\u2588\u2588" + ChatColor.GRAY + "\u2588\u2588\u2588" + " " + ChatColor.YELLOW + playerName + " won the event!",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588",
            ChatColor.GRAY + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GRAY + "\u2588",
            ChatColor.GRAY + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588"
        };
    }
}