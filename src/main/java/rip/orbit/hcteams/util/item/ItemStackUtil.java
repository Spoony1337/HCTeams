package rip.orbit.hcteams.util.item;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemStackUtil
{
    public static ItemStack LEATHER_HELMET;
    public static ItemStack LEATHER_CHESTPLATE;
    public static ItemStack LEATHER_LEGGINGS;
    public static ItemStack LEATHER_BOOTS;
    public static ItemStack CHAINMAIL_HELMET;
    public static ItemStack CHAINMAIL_CHESTPLATE;
    public static ItemStack CHAINMAIL_LEGGINGS;
    public static ItemStack CHAINMAIL_BOOTS;
    public static ItemStack IRON_HELMET;
    public static ItemStack IRON_CHESTPLATE;
    public static ItemStack IRON_LEGGINGS;
    public static ItemStack IRON_BOOTS;
    public static ItemStack GOLD_HELMET;
    public static ItemStack GOLD_CHESTPLATE;
    public static ItemStack GOLD_LEGGINGS;
    public static ItemStack GOLD_BOOTS;
    public static ItemStack DIAMOND_HELMET;
    public static ItemStack DIAMOND_CHESTPLATE;
    public static ItemStack DIAMOND_LEGGINGS;
    public static ItemStack DIAMOND_BOOTS;
    public static ItemStack IRON_BARDING;
    public static ItemStack GOLD_BARDING;
    public static ItemStack DIAMOND_BARDING;
    public static ItemStack SHIELD;
    public static ItemStack ELYTRA;
    public static ItemStack SPECTRAL_ARROW;
    public static ItemStack TIPPED_ARROW;
    public static ItemStack BOW;
    public static ItemStack ARROW;
    public static ItemStack FISHING_ROD;
    public static ItemStack WOOD_AXE;
    public static ItemStack WOOD_SWORD;
    public static ItemStack STONE_AXE;
    public static ItemStack STONE_SWORD;
    public static ItemStack IRON_AXE;
    public static ItemStack IRON_SWORD;
    public static ItemStack GOLD_AXE;
    public static ItemStack GOLD_SWORD;
    public static ItemStack DIAMOND_AXE;
    public static ItemStack DIAMOND_SWORD;
    public static ItemStack COAL;
    public static ItemStack IRON_INGOT;
    public static ItemStack GOLD_INGOT;
    public static ItemStack REDSTONE;
    public static ItemStack DIAMOND;
    public static ItemStack GLOWSTONE_DUST;
    public static ItemStack ENDER_PEARL;
    public static ItemStack GOLDEN_APPLE;
    public static ItemStack GOD_APPLE;
    public static ItemStack MILK_BUCKET;
    public static ItemStack APPLE;
    public static ItemStack BAKED_POTATO;
    public static ItemStack BREAD;
    public static ItemStack COOKED_BEEF;
    public static ItemStack COOKED_CHICKEN;
    public static ItemStack COOKED_FISH;
    public static ItemStack COOKIE;
    public static ItemStack GRILLED_PORK;
    public static ItemStack MELON;
    public static ItemStack PUMPKIN_PIE;
    public static ItemStack GOLDEN_CARROT;
    public static ItemStack MUSHROOM_SOUP;
    public static ItemStack CAKE;
    public static ItemStack EMPTY_ITEM;
    public static ItemStack SADDLE;
    public static ItemStack WEB;
    public static ItemStack FLINT_AND_STEEL;
    public static ItemStack EXP_BOTTLE;
    public static ItemStack SULPHUR;
    public static ItemStack ANVIL;
    public static ItemStack POTION;
    public static ItemStack REGENERATION_POTION;
    public static ItemStack SWIFTNESS_POTION;
    public static ItemStack FIRE_RESISTANCE_POTION;
    public static ItemStack POISON_POTION;
    public static ItemStack HEALING_POTION;
    public static ItemStack NIGHT_VISION_POTION;
    public static ItemStack WEAKNESS_POTION;
    public static ItemStack STRENGTH_POTION;
    public static ItemStack LEAPING_POTION;
    public static ItemStack SLOWNESS_POTION;
    public static ItemStack HARMING_POTION;
    public static ItemStack WATER_BREATHING_POTION;
    public static ItemStack INVISIBILITY_POTION;
    public static ItemStack REGENERATION_POTION_II;
    public static ItemStack SWIFTNESS_POTION_II;
    public static ItemStack POISON_POTION_II;
    public static ItemStack HEALING_POTION_II;
    public static ItemStack STRENGTH_POTION_II;
    public static ItemStack LEAPING_POTION_II;
    public static ItemStack HARMING_POTION_II;
    public static ItemStack REGENERATION_POTION_EXT;
    public static ItemStack SWIFTNESS_POTION_EXT;
    public static ItemStack FIRE_RESISTANCE_POTION_EXT;
    public static ItemStack POISON_POTION_EXT;
    public static ItemStack NIGHT_VISION_POTION_EXT;
    public static ItemStack WEAKNESS_POTION_EXT;
    public static ItemStack STRENGTH_POTION_EXT;
    public static ItemStack SLOWNESS_POTION_EXT;
    public static ItemStack LEAPING_POTION_EXT;
    public static ItemStack WATER_BREATHING_POTION_EXT;
    public static ItemStack INVISIBILITY_POTION_EXT;
    public static ItemStack REGENERATION_POTION_II_EXT;
    public static ItemStack SWIFTNESS_POTION_II_EXT;
    public static ItemStack POISON_POTION_II_EXT;
    public static ItemStack STRENGTH_POTION_II_EXT;
    public static ItemStack REGENERATION_SPLASH;
    public static ItemStack SWIFTNESS_SPLASH;
    public static ItemStack FIRE_RESISTANCE_SPLASH;
    public static ItemStack POISON_SPLASH;
    public static ItemStack HEALING_SPLASH;
    public static ItemStack NIGHT_VISION_SPLASH;
    public static ItemStack WEAKNESS_SPLASH;
    public static ItemStack STRENGTH_SPLASH;
    public static ItemStack SLOWNESS_SPLASH;
    public static ItemStack HARMING_SPLASH;
    public static ItemStack BREATHING_SPLASH;
    public static ItemStack INVISIBILITY_SPLASH;
    public static ItemStack REGENERATION_SPLASH_II;
    public static ItemStack SWIFTNESS_SPLASH_II;
    public static ItemStack POISON_SPLASH_II;
    public static ItemStack HEALING_SPLASH_II;
    public static ItemStack STRENGTH_SPLASH_II;
    public static ItemStack LEAPING_SPLASH_II;
    public static ItemStack HARMING_SPLASH_II;
    public static ItemStack REGENERATION_SPLASH_EXT;
    public static ItemStack SWIFTNESS_SPLASH_EXT;
    public static ItemStack FIRE_RESISTANCE_SPLASH_EXT;
    public static ItemStack POISON_SPLASH_EXT;
    public static ItemStack NIGHT_VISION_SPLASH_EXT;
    public static ItemStack WEAKNESS_SPLASH_EXT;
    public static ItemStack STRENGTH_SPLASH_EXT;
    public static ItemStack SLOWNESS_SPLASH_EXT;
    public static ItemStack LEAPING_SPLASH_EXT;
    public static ItemStack BREATHING_SPLASH_EXT;
    public static ItemStack INVISIBILITY_SPLASH_EXT;
    public static ItemStack REGENERATION_SPLASH_II_EXT;
    public static ItemStack POISON_SPLASH_II_EXT;
    public static ItemStack STRENGTH_SPLASH_II_EXT;
    public static ItemStack POISON_LINGERING;
    
    public static ItemStack[] LEATHER_ARMOR() {
        return new ItemStack[] { ItemStackUtil.LEATHER_HELMET, ItemStackUtil.LEATHER_CHESTPLATE, ItemStackUtil.LEATHER_LEGGINGS, ItemStackUtil.LEATHER_BOOTS };
    }
    
    public static ItemStack[] CHAINMAIL_ARMOR() {
        return new ItemStack[] { ItemStackUtil.CHAINMAIL_HELMET, ItemStackUtil.CHAINMAIL_CHESTPLATE, ItemStackUtil.CHAINMAIL_LEGGINGS, ItemStackUtil.CHAINMAIL_BOOTS };
    }
    
    public static ItemStack[] IRON_ARMOR() {
        return new ItemStack[] { ItemStackUtil.IRON_HELMET, ItemStackUtil.IRON_CHESTPLATE, ItemStackUtil.IRON_LEGGINGS, ItemStackUtil.IRON_BOOTS };
    }
    
    public static ItemStack[] GOLD_ARMOR() {
        return new ItemStack[] { ItemStackUtil.GOLD_HELMET, ItemStackUtil.GOLD_CHESTPLATE, ItemStackUtil.GOLD_LEGGINGS, ItemStackUtil.GOLD_BOOTS };
    }
    
    public static ItemStack[] DIAMOND_ARMOR() {
        return new ItemStack[] { ItemStackUtil.DIAMOND_HELMET, ItemStackUtil.DIAMOND_CHESTPLATE, ItemStackUtil.DIAMOND_LEGGINGS, ItemStackUtil.DIAMOND_BOOTS };
    }
    
    public static ItemStack[] ALL_ARMOR() {
        return new ItemStack[] { ItemStackUtil.LEATHER_HELMET, ItemStackUtil.LEATHER_CHESTPLATE, ItemStackUtil.LEATHER_LEGGINGS, ItemStackUtil.LEATHER_BOOTS, ItemStackUtil.CHAINMAIL_HELMET, ItemStackUtil.CHAINMAIL_CHESTPLATE, ItemStackUtil.CHAINMAIL_LEGGINGS, ItemStackUtil.CHAINMAIL_BOOTS, ItemStackUtil.IRON_HELMET, ItemStackUtil.IRON_CHESTPLATE, ItemStackUtil.IRON_LEGGINGS, ItemStackUtil.IRON_BOOTS, ItemStackUtil.GOLD_HELMET, ItemStackUtil.GOLD_CHESTPLATE, ItemStackUtil.GOLD_LEGGINGS, ItemStackUtil.GOLD_BOOTS, ItemStackUtil.DIAMOND_HELMET, ItemStackUtil.DIAMOND_CHESTPLATE, ItemStackUtil.DIAMOND_LEGGINGS, ItemStackUtil.DIAMOND_BOOTS };
    }
    
    public static ItemStack[] WOOD_WEAPONS() {
        return new ItemStack[] { ItemStackUtil.WOOD_AXE, ItemStackUtil.WOOD_SWORD };
    }
    
    public static ItemStack[] STONE_WEAPONS() {
        return new ItemStack[] { ItemStackUtil.STONE_AXE, ItemStackUtil.STONE_SWORD };
    }
    
    public static ItemStack[] IRON_WEAPONS() {
        return new ItemStack[] { ItemStackUtil.IRON_AXE, ItemStackUtil.IRON_SWORD };
    }
    
    public static ItemStack[] GOLD_WEAPONS() {
        return new ItemStack[] { ItemStackUtil.GOLD_AXE, ItemStackUtil.GOLD_SWORD };
    }
    
    public static ItemStack[] DIAMOND_WEAPONS() {
        return new ItemStack[] { ItemStackUtil.DIAMOND_AXE, ItemStackUtil.DIAMOND_SWORD };
    }
    
    public static ItemStack[] ALL_WEAPONS() {
        return new ItemStack[] { ItemStackUtil.BOW, ItemStackUtil.ARROW, ItemStackUtil.WOOD_AXE, ItemStackUtil.WOOD_SWORD, ItemStackUtil.STONE_AXE, ItemStackUtil.STONE_SWORD, ItemStackUtil.IRON_AXE, ItemStackUtil.IRON_SWORD, ItemStackUtil.GOLD_AXE, ItemStackUtil.GOLD_SWORD, ItemStackUtil.DIAMOND_AXE, ItemStackUtil.DIAMOND_SWORD, ItemStackUtil.FISHING_ROD };
    }
    
    public static ItemStack[] ALL_FOOD() {
        return new ItemStack[] { ItemStackUtil.APPLE, ItemStackUtil.BAKED_POTATO, ItemStackUtil.BREAD, ItemStackUtil.COOKED_BEEF, ItemStackUtil.COOKED_CHICKEN, ItemStackUtil.COOKED_FISH, ItemStackUtil.COOKIE, ItemStackUtil.GRILLED_PORK, ItemStackUtil.MELON, ItemStackUtil.PUMPKIN_PIE, ItemStackUtil.GOLDEN_CARROT, ItemStackUtil.MUSHROOM_SOUP };
    }
    
    public static ItemStack createItem(final Material material, final String displayName, final String... lore) {
        final List<String> loreList = new ArrayList<>();
        Collections.addAll(loreList, lore);
        return createItem(material, 1, (short)0, displayName, loreList);
    }
    
    public static ItemStack createItem(final Material material, final String displayName, final List<String> lore) {
        return createItem(material, 1, (short)0, displayName, lore);
    }
    
    public static ItemStack createItem(final Material material, final int amount) {
        return createItem(material, amount, null, null, null);
    }
    
    public static ItemStack createItem(final Material material, final short data) {
        return createItem(material, data, null, null, null);
    }
    
    public static ItemStack createItem(final Material material, final int amount, final String displayName, final String... lore) {
        final List<String> loreList = new ArrayList<>();
        Collections.addAll(loreList, lore);
        return createItem(material, amount, (short)0, displayName, loreList);
    }
    
    public static ItemStack createItem(final Material material, final int amount, final String displayName, final List<String> lore) {
        return createItem(material, amount, (short)0, displayName, lore);
    }
    
    public static ItemStack createItem(final Material material, final short data, final String displayName, final String... lore) {
        final List<String> loreList = new ArrayList<>();
        Collections.addAll(loreList, lore);
        return createItem(material, 1, data, displayName, loreList);
    }
    
    public static ItemStack createItem(final Material material, final short data, final String displayName, final List<String> lore) {
        return createItem(material, 1, data, displayName, lore);
    }
    
    public static ItemStack createItem(final Material material, final int amount, final short data, final String displayName, final String... lore) {
        final List<String> list = new ArrayList<>();
        Collections.addAll(list, lore);
        return createItem(material, amount, data, displayName, list);
    }
    
    public static ItemStack createItem(final Material material, final int amount, final short data, final String displayName, final List<String> lore) {
        final ItemStack item = new ItemStack(material, amount, data);
        final ItemMeta itemMeta = item.getItemMeta();
        if (displayName != null) {
            itemMeta.setDisplayName(displayName);
        }
        if (lore != null) {
            itemMeta.setLore((List)lore);
        }
        item.setItemMeta(itemMeta);
        return item;
    }
    
    public static ItemStack setItemTitle(final ItemStack item, final String title) {
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(title);
        item.setItemMeta(itemMeta);
        return item;
    }
    
    public static ItemStack setLore(final ItemStack item, final String... lore) {
        final List<String> list = new ArrayList<>();
        Collections.addAll(list, lore);
        return setLore(item, list);
    }
    
    public static ItemStack setLore(final ItemStack item, final List<String> lore) {
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore((List)lore);
        item.setItemMeta(itemMeta);
        return item;
    }
    
    public static ItemStack createGoldenHead() {
        return createGoldenHead(1);
    }
    
    public static ItemStack createGoldenHead(final int amount) {
        final ItemStack apple = new ItemStack(Material.GOLDEN_APPLE, amount);
        final ItemMeta appleMeta = apple.getItemMeta();
        appleMeta.setDisplayName(ChatColor.GOLD + "Golden Head");
        final ArrayList<String> lore = new ArrayList<>();
        lore.add("Some say consuming the head of a");
        lore.add("fallen foe strengthens the blood");
        appleMeta.setLore((List)lore);
        apple.setItemMeta(appleMeta);
        return apple;
    }
    
    public static ItemStack createEnchantmentBook(final Enchantment enchant, final int level) {
        final ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        final EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
        meta.addStoredEnchant(enchant, level, true);
        item.setItemMeta((ItemMeta)meta);
        return item;
    }
    
    public static boolean equals(final ItemStack item, final ItemStack other) {
        return item != null && other != null && item.getType() == other.getType() && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(other.getItemMeta().getDisplayName()) && item.getItemMeta().getLore().equals(other.getItemMeta().getLore());
    }
    
    public static ItemStack addUnbreaking(final ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.spigot().setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public static void addUnbreakingToArmor(final Player player) {
        for (final ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack != null) {
                if (itemStack.getType() != Material.AIR) {
                    addUnbreaking(itemStack);
                }
            }
        }
    }
    
    public static void addUnbreakingToWeapons(final Player player) {
        for (final ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null) {
                if (itemStack.getType() != Material.AIR) {
                    final Material type = itemStack.getType();
                    if (type == Material.WOOD_SWORD || type == Material.STONE_SWORD || type == Material.GOLD_SWORD || type == Material.IRON_SWORD || type == Material.DIAMOND_SWORD || type == Material.BOW) {
                        addUnbreaking(itemStack);
                    }
                }
            }
        }
    }
    
    public static ItemStack removeUnbreaking(final ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.spigot().setUnbreakable(false);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
    public static void removeUnbreakingFromArmor(final Player player) {
        for (final ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack != null) {
                if (itemStack.getType() != Material.AIR) {
                    removeUnbreaking(itemStack);
                }
            }
        }
    }
    
    public static void removeUnbreakingFromWeapons(final Player player) {
        for (final ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null) {
                if (itemStack.getType() != Material.AIR) {
                    final Material type = itemStack.getType();
                    if (type == Material.WOOD_SWORD || type == Material.STONE_SWORD || type == Material.GOLD_SWORD || type == Material.IRON_SWORD || type == Material.DIAMOND_SWORD || type == Material.BOW) {
                        removeUnbreaking(itemStack);
                    }
                }
            }
        }
    }
    
    static {
        ItemStackUtil.LEATHER_HELMET = new ItemStack(Material.LEATHER_HELMET);
        ItemStackUtil.LEATHER_CHESTPLATE = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStackUtil.LEATHER_LEGGINGS = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStackUtil.LEATHER_BOOTS = new ItemStack(Material.LEATHER_BOOTS);
        ItemStackUtil.CHAINMAIL_HELMET = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemStackUtil.CHAINMAIL_CHESTPLATE = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemStackUtil.CHAINMAIL_LEGGINGS = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemStackUtil.CHAINMAIL_BOOTS = new ItemStack(Material.CHAINMAIL_BOOTS);
        ItemStackUtil.IRON_HELMET = new ItemStack(Material.IRON_HELMET);
        ItemStackUtil.IRON_CHESTPLATE = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStackUtil.IRON_LEGGINGS = new ItemStack(Material.IRON_LEGGINGS);
        ItemStackUtil.IRON_BOOTS = new ItemStack(Material.IRON_BOOTS);
        ItemStackUtil.GOLD_HELMET = new ItemStack(Material.GOLD_HELMET);
        ItemStackUtil.GOLD_CHESTPLATE = new ItemStack(Material.GOLD_CHESTPLATE);
        ItemStackUtil.GOLD_LEGGINGS = new ItemStack(Material.GOLD_LEGGINGS);
        ItemStackUtil.GOLD_BOOTS = new ItemStack(Material.GOLD_BOOTS);
        ItemStackUtil.DIAMOND_HELMET = new ItemStack(Material.DIAMOND_HELMET);
        ItemStackUtil.DIAMOND_CHESTPLATE = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStackUtil.DIAMOND_LEGGINGS = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStackUtil.DIAMOND_BOOTS = new ItemStack(Material.DIAMOND_BOOTS);
        ItemStackUtil.IRON_BARDING = new ItemStack(Material.IRON_BARDING);
        ItemStackUtil.GOLD_BARDING = new ItemStack(Material.GOLD_BARDING);
        ItemStackUtil.DIAMOND_BARDING = new ItemStack(Material.DIAMOND_BARDING);
        ItemStackUtil.BOW = new ItemStack(Material.BOW);
        ItemStackUtil.ARROW = new ItemStack(Material.ARROW);
        ItemStackUtil.FISHING_ROD = new ItemStack(Material.FISHING_ROD);
        ItemStackUtil.WOOD_AXE = new ItemStack(Material.WOOD_AXE);
        ItemStackUtil.WOOD_SWORD = new ItemStack(Material.WOOD_SWORD);
        ItemStackUtil.STONE_AXE = new ItemStack(Material.STONE_AXE);
        ItemStackUtil.STONE_SWORD = new ItemStack(Material.STONE_SWORD);
        ItemStackUtil.IRON_AXE = new ItemStack(Material.IRON_AXE);
        ItemStackUtil.IRON_SWORD = new ItemStack(Material.IRON_SWORD);
        ItemStackUtil.GOLD_AXE = new ItemStack(Material.GOLD_AXE);
        ItemStackUtil.GOLD_SWORD = new ItemStack(Material.GOLD_SWORD);
        ItemStackUtil.DIAMOND_AXE = new ItemStack(Material.DIAMOND_AXE);
        ItemStackUtil.DIAMOND_SWORD = new ItemStack(Material.DIAMOND_SWORD);
        ItemStackUtil.COAL = new ItemStack(Material.COAL);
        ItemStackUtil.IRON_INGOT = new ItemStack(Material.IRON_INGOT);
        ItemStackUtil.GOLD_INGOT = new ItemStack(Material.GOLD_INGOT);
        ItemStackUtil.REDSTONE = new ItemStack(Material.REDSTONE);
        ItemStackUtil.DIAMOND = new ItemStack(Material.DIAMOND);
        ItemStackUtil.GLOWSTONE_DUST = new ItemStack(Material.GLOWSTONE_DUST);
        ItemStackUtil.ENDER_PEARL = new ItemStack(Material.ENDER_PEARL);
        ItemStackUtil.GOLDEN_APPLE = new ItemStack(Material.GOLDEN_APPLE);
        ItemStackUtil.GOD_APPLE = new ItemStack(Material.GOLDEN_APPLE, 1, (short)1);
        ItemStackUtil.MILK_BUCKET = new ItemStack(Material.MILK_BUCKET);
        ItemStackUtil.APPLE = new ItemStack(Material.APPLE);
        ItemStackUtil.BAKED_POTATO = new ItemStack(Material.BAKED_POTATO);
        ItemStackUtil.BREAD = new ItemStack(Material.BREAD);
        ItemStackUtil.COOKED_BEEF = new ItemStack(Material.COOKED_BEEF);
        ItemStackUtil.COOKED_CHICKEN = new ItemStack(Material.COOKED_CHICKEN);
        ItemStackUtil.COOKED_FISH = new ItemStack(Material.COOKED_FISH);
        ItemStackUtil.COOKIE = new ItemStack(Material.COOKIE);
        ItemStackUtil.GRILLED_PORK = new ItemStack(Material.GRILLED_PORK);
        ItemStackUtil.MELON = new ItemStack(Material.MELON);
        ItemStackUtil.PUMPKIN_PIE = new ItemStack(Material.PUMPKIN_PIE);
        ItemStackUtil.GOLDEN_CARROT = new ItemStack(Material.GOLDEN_CARROT);
        ItemStackUtil.MUSHROOM_SOUP = new ItemStack(Material.MUSHROOM_SOUP);
        ItemStackUtil.CAKE = new ItemStack(Material.CAKE);
        ItemStackUtil.EMPTY_ITEM = new ItemStack(Material.AIR);
        ItemStackUtil.SADDLE = new ItemStack(Material.SADDLE);
        ItemStackUtil.WEB = new ItemStack(Material.WEB);
        ItemStackUtil.FLINT_AND_STEEL = new ItemStack(Material.FLINT_AND_STEEL);
        ItemStackUtil.EXP_BOTTLE = new ItemStack(Material.EXP_BOTTLE);
        ItemStackUtil.SULPHUR = new ItemStack(Material.SULPHUR);
        ItemStackUtil.ANVIL = new ItemStack(Material.ANVIL);
        ItemStackUtil.POTION = new ItemStack(Material.POTION);
        ItemStackUtil.REGENERATION_POTION = new ItemStack(Material.POTION, 1, (short)8193);
        ItemStackUtil.SWIFTNESS_POTION = new ItemStack(Material.POTION, 1, (short)8194);
        ItemStackUtil.FIRE_RESISTANCE_POTION = new ItemStack(Material.POTION, 1, (short)8227);
        ItemStackUtil.POISON_POTION = new ItemStack(Material.POTION, 1, (short)8196);
        ItemStackUtil.HEALING_POTION = new ItemStack(Material.POTION, 1, (short)8261);
        ItemStackUtil.NIGHT_VISION_POTION = new ItemStack(Material.POTION, 1, (short)8230);
        ItemStackUtil.WEAKNESS_POTION = new ItemStack(Material.POTION, 1, (short)8232);
        ItemStackUtil.STRENGTH_POTION = new ItemStack(Material.POTION, 1, (short)8201);
        ItemStackUtil.LEAPING_POTION = new ItemStack(Material.POTION, 1, (short)8202);
        ItemStackUtil.SLOWNESS_POTION = new ItemStack(Material.POTION, 1, (short)8234);
        ItemStackUtil.HARMING_POTION = new ItemStack(Material.POTION, 1, (short)8268);
        ItemStackUtil.WATER_BREATHING_POTION = new ItemStack(Material.POTION, 1, (short)8237);
        ItemStackUtil.INVISIBILITY_POTION = new ItemStack(Material.POTION, 1, (short)8238);
        ItemStackUtil.REGENERATION_POTION_II = new ItemStack(Material.POTION, 1, (short)8225);
        ItemStackUtil.SWIFTNESS_POTION_II = new ItemStack(Material.POTION, 1, (short)8226);
        ItemStackUtil.POISON_POTION_II = new ItemStack(Material.POTION, 1, (short)8228);
        ItemStackUtil.HEALING_POTION_II = new ItemStack(Material.POTION, 1, (short)8229);
        ItemStackUtil.STRENGTH_POTION_II = new ItemStack(Material.POTION, 1, (short)8233);
        ItemStackUtil.LEAPING_POTION_II = new ItemStack(Material.POTION, 1, (short)8235);
        ItemStackUtil.HARMING_POTION_II = new ItemStack(Material.POTION, 1, (short)8236);
        ItemStackUtil.REGENERATION_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8257);
        ItemStackUtil.SWIFTNESS_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8258);
        ItemStackUtil.FIRE_RESISTANCE_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8259);
        ItemStackUtil.POISON_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8260);
        ItemStackUtil.NIGHT_VISION_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8262);
        ItemStackUtil.WEAKNESS_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8264);
        ItemStackUtil.STRENGTH_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8265);
        ItemStackUtil.SLOWNESS_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8266);
        ItemStackUtil.LEAPING_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8267);
        ItemStackUtil.WATER_BREATHING_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8269);
        ItemStackUtil.INVISIBILITY_POTION_EXT = new ItemStack(Material.POTION, 1, (short)8270);
        ItemStackUtil.REGENERATION_POTION_II_EXT = new ItemStack(Material.POTION, 1, (short)8289);
        ItemStackUtil.SWIFTNESS_POTION_II_EXT = new ItemStack(Material.POTION, 1, (short)8290);
        ItemStackUtil.POISON_POTION_II_EXT = new ItemStack(Material.POTION, 1, (short)8292);
        ItemStackUtil.STRENGTH_POTION_II_EXT = new ItemStack(Material.POTION, 1, (short)8297);
        ItemStackUtil.REGENERATION_SPLASH = new ItemStack(Material.POTION, 1, (short)16385);
        ItemStackUtil.SWIFTNESS_SPLASH = new ItemStack(Material.POTION, 1, (short)16386);
        ItemStackUtil.FIRE_RESISTANCE_SPLASH = new ItemStack(Material.POTION, 1, (short)16387);
        ItemStackUtil.POISON_SPLASH = new ItemStack(Material.POTION, 1, (short)16388);
        ItemStackUtil.HEALING_SPLASH = new ItemStack(Material.POTION, 1, (short)16453);
        ItemStackUtil.NIGHT_VISION_SPLASH = new ItemStack(Material.POTION, 1, (short)16390);
        ItemStackUtil.WEAKNESS_SPLASH = new ItemStack(Material.POTION, 1, (short)16392);
        ItemStackUtil.STRENGTH_SPLASH = new ItemStack(Material.POTION, 1, (short)16393);
        ItemStackUtil.SLOWNESS_SPLASH = new ItemStack(Material.POTION, 1, (short)16394);
        ItemStackUtil.HARMING_SPLASH = new ItemStack(Material.POTION, 1, (short)16396);
        ItemStackUtil.BREATHING_SPLASH = new ItemStack(Material.POTION, 1, (short)16397);
        ItemStackUtil.INVISIBILITY_SPLASH = new ItemStack(Material.POTION, 1, (short)16398);
        ItemStackUtil.REGENERATION_SPLASH_II = new ItemStack(Material.POTION, 1, (short)16417);
        ItemStackUtil.SWIFTNESS_SPLASH_II = new ItemStack(Material.POTION, 1, (short)16418);
        ItemStackUtil.POISON_SPLASH_II = new ItemStack(Material.POTION, 1, (short)16420);
        ItemStackUtil.HEALING_SPLASH_II = new ItemStack(Material.POTION, 1, (short)16421);
        ItemStackUtil.STRENGTH_SPLASH_II = new ItemStack(Material.POTION, 1, (short)16425);
        ItemStackUtil.LEAPING_SPLASH_II = new ItemStack(Material.POTION, 1, (short)16427);
        ItemStackUtil.HARMING_SPLASH_II = new ItemStack(Material.POTION, 1, (short)16428);
        ItemStackUtil.REGENERATION_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16449);
        ItemStackUtil.SWIFTNESS_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16450);
        ItemStackUtil.FIRE_RESISTANCE_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16451);
        ItemStackUtil.POISON_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16452);
        ItemStackUtil.NIGHT_VISION_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16454);
        ItemStackUtil.WEAKNESS_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16456);
        ItemStackUtil.STRENGTH_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16457);
        ItemStackUtil.SLOWNESS_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16458);
        ItemStackUtil.LEAPING_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16459);
        ItemStackUtil.BREATHING_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16461);
        ItemStackUtil.INVISIBILITY_SPLASH_EXT = new ItemStack(Material.POTION, 1, (short)16481);
        ItemStackUtil.REGENERATION_SPLASH_II_EXT = new ItemStack(Material.POTION, 1, (short)16482);
        ItemStackUtil.POISON_SPLASH_II_EXT = new ItemStack(Material.POTION, 1, (short)16484);
        ItemStackUtil.STRENGTH_SPLASH_II_EXT = new ItemStack(Material.POTION, 1, (short)16489);
    }
}
