package rip.orbit.hcteams.pvpclasses;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.HCF;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class PvPClass implements Listener {

    @Getter String name;
    @Getter int warmup;
    @Getter List<Material> consumables;

    private static Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

    public PvPClass(String name, int warmup, List<Material> consumables) {
        this.name = name;
        this.warmup = warmup;
        this.consumables = consumables;

        // Reduce warmup on kit maps
        if (HCF.getInstance().getMapHandler().isKitMap()) {
            this.warmup = 5;
        }
    }

    public void apply(Player player) {

    }

    public void tick(Player player) {

    }

    public void remove(Player player) {

    }

    public boolean canApply(Player player) {
        return (true);
    }

    /*
    /*public static void removeInfiniteEffects(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (Foxtrot.getInstance().getServer().getPluginManager().isPluginEnabled("qEnchants")) {
                restores.remove(player.getUniqueId(), potionEffect.getType());

                if (potionEffect.getDuration() > 1639000) {
                    player.removePotionEffect(potionEffect.getType());
                }

                if (player.getInventory().getBoots() != null && player.getInventory().getBoots().getItemMeta().getLore() != null) {
                    if (player.getInventory().getBoots().getItemMeta().getLore().get(0).equalsIgnoreCase(CC.translate("&cSpeed II"))) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED ,1, 1000000));
                    }

                    /*for (ItemStack armour : player.getInventory().getArmorContents()) {
                        if (Enchan)
                    }*/

    //}
    //}
    //}
    //}

    public static void removeInfiniteEffects(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1_000_000) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public boolean itemConsumed(Player player, Material type) {
        return (true);
    }

    public abstract boolean qualifies(PlayerInventory armor);

    protected boolean wearingAllArmor(PlayerInventory armor) {
        return (armor.getHelmet() != null &&
                armor.getChestplate() != null &&
                armor.getLeggings() != null &&
                armor.getBoots() != null);
    }

    public static void smartAddPotion(Player player, PotionEffect potionEffect) {
        setRestoreEffect(player, potionEffect);
    }

    @AllArgsConstructor
    public static class SavedPotion {

        @Getter PotionEffect potionEffect;
        @Getter long time;
        @Getter private boolean perm;

    }

    public static void setRestoreEffect(Player player, PotionEffect effect) {
        boolean shouldCancel = true;
        Collection<PotionEffect> activeList = player.getActivePotionEffects();
        for (PotionEffect active : activeList) {
            if (!active.getType().equals(effect.getType())) continue;

            // If the current potion effect has a higher amplifier, ignore this one.
            if (effect.getAmplifier() < active.getAmplifier()) {
                return;
            } else if (effect.getAmplifier() == active.getAmplifier()) {
                // If the current potion effect has a longer duration, ignore this one.
                if (0 < active.getDuration() && (effect.getDuration() <= active.getDuration() || effect.getDuration() - active.getDuration() < 10)) {
                    return;
                }
            }

            restores.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;
            break;
        }

        // Cancel the previous restore.
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > 120 && effect.getDuration() < 9600) {
            restores.remove(player.getUniqueId(), effect.getType());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player) {
            Player player = (Player) livingEntity;
            PotionEffect previous = restores.remove(player.getUniqueId(), event.getEffect().getType());
            if (previous != null) {
                event.setCancelled(true);
                player.addPotionEffect(previous, true);
                //PvPClass.removeInfiniteEffects(player);
                Bukkit.getLogger().info("Restored " + previous.getType().toString() + " for " + player.getName() + ". duration: " + previous.getDuration() + ". amp: " + previous.getAmplifier());
            }
        }
    }

}
