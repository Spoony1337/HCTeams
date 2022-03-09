package rip.orbit.hcteams.listener.fixes;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;

import java.util.HashSet;

public class EnchantmentLimiterListeners implements Listener {

    public int getEnchantmentLimit(Enchantment enchantment) {
        if (HCF.getInstance().getConfig().contains("ENCHANTMENT_LIMITER." + enchantment.getName())) {
            return HCF.getInstance().getConfig().getInt("ENCHANTMENT_LIMITER." + enchantment.getName());
        }

        return enchantment.getMaxLevel();
    }


    @EventHandler
    public void onEnchantItemtEvent(EnchantItemEvent event) {
        for (Enchantment enchantment : new HashSet<>(event.getEnchantsToAdd().keySet())) {
            int limit = getEnchantmentLimit(enchantment);
            int level = event.getEnchantsToAdd().get(enchantment);

            if (level > limit) {

                event.getEnchantsToAdd().remove(enchantment);
                if (limit != 0) {
                    event.getEnchantsToAdd().put(enchantment, limit);
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        if (inventory.getType() == InventoryType.ANVIL && event.getRawSlot() == 2) {
            ItemStack result = inventory.getItem(2);
            if (result != null) {
                event.setCancelled(true);

                for (Enchantment enchantment : new HashSet<>(result.getEnchantments().keySet())) {
                    int limit = getEnchantmentLimit(enchantment);
                    int level = result.getEnchantmentLevel(enchantment);

                    if (level > limit) {

                        result.removeEnchantment(enchantment);
                        if (limit != 0) {
                            result.addEnchantment(enchantment, limit);
                        }

                        player.updateInventory();
                    }
                }

                inventory.setItem(0, new ItemStack(Material.AIR));
                inventory.setItem(1, new ItemStack(Material.AIR));
                inventory.setItem(2, new ItemStack(Material.AIR));

                if (event.getClick().name().contains("SHIFT")) {
                    player.getInventory().addItem(result);
                } else {
                    event.setCursor(result);
                }
            }
        }
    }

}
