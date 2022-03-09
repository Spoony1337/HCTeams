package rip.orbit.hcteams.pvpclasses.pvpclasses;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.listener.FoxListener;
import rip.orbit.hcteams.pvpclasses.PvPClass;
import rip.orbit.hcteams.pvpclasses.PvPClassHandler;
import rip.orbit.hcteams.pvpclasses.pvpclasses.bard.BardEffect;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BardClass extends PvPClass implements Listener {
    /*
            Things commented with // CUSTOM
            are the 'unique' abilities, or things that have custom behaviour not seen by most other effects.
            An example is invis, whose passive cannot be used while its click is on cooldown.
            This is therefore commented with // CUSTOM
     */

    public Map<Material, BardEffect> BARD_CLICK_EFFECTS = new HashMap<>();
    public Map<Material, BardEffect> BARD_PASSIVE_EFFECTS = new HashMap<>();

    @Getter
    private static Map<String, Long> lastEffectUsage = new ConcurrentHashMap<>();
    @Getter
    private static Map<String, Float> energy = new ConcurrentHashMap<>();

    public static int BARD_RANGE = 40;
    public static int EFFECT_COOLDOWN = 10 * 1000;
    public static float MAX_ENERGY = 120;
    public static float ENERGY_REGEN_PER_SECOND = 1;

    public BardClass() {
        super("Bard", 15, null);

        // Passive buffs
        BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.SUGAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.FEATHER, BardEffect.fromPotion(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotion(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotion(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotion(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.INK_SACK, BardEffect.fromPotion(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 6, 0)));

        // Click buffs
        BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), 45));
        BARD_CLICK_EFFECTS.put(Material.SUGAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2), 20));
        BARD_CLICK_EFFECTS.put(Material.FEATHER, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6), 25));
        BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0), 40));
        BARD_CLICK_EFFECTS.put(Material.INK_SACK, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 45, 1), 35));
        BARD_CLICK_EFFECTS.put(Material.WHEAT, BardEffect.fromEnergy(25));

        // Click debuffs
        BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, BardEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1), 35));


        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                    if (!PvPClassHandler.hasKitOn(player, BardClass.this) || HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        continue;
                    }

                    if (energy.containsKey(player.getName())) {
                        if (energy.get(player.getName()) == MAX_ENERGY) {
                            continue;
                        }

                        energy.put(player.getName(), Math.min(MAX_ENERGY, energy.get(player.getName()) + ENERGY_REGEN_PER_SECOND));
                    } else {
                        energy.put(player.getName(), 0F);
                    }

                    int manaInt = energy.get(player.getName()).intValue();

                    if (manaInt % 10 == 0) {
                        player.sendMessage(ChatColor.AQUA + "Bard Energy: " + ChatColor.GREEN + manaInt);
                    }
                }
            }

        }.runTaskTimer(HCF.getInstance(), 15L, 20L);
    }

    @Override
    public boolean qualifies(PlayerInventory armor) {
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.GOLD_HELMET &&
                armor.getChestplate().getType() == Material.GOLD_CHESTPLATE &&
                armor.getLeggings().getType() == Material.GOLD_LEGGINGS &&
                armor.getBoots().getType() == Material.GOLD_BOOTS;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);

        if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
        }
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        }

        if (player.getItemInHand() != null && BARD_PASSIVE_EFFECTS.containsKey(player.getItemInHand().getType()) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            // CUSTOM
            if (player.getItemInHand().getType() == Material.FERMENTED_SPIDER_EYE && getLastEffectUsage().containsKey(player.getName()) && getLastEffectUsage().get(player.getName()) > System.currentTimeMillis()) {
                return;
            }

            giveBardEffect(player, BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true, false);
        }
        super.tick(player);
    }


    @Override
    public void remove(Player player) {
        energy.remove(player.getName());

        for (BardEffect bardEffect : BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }

        for (BardEffect bardEffect : BARD_CLICK_EFFECTS.values()) {
            bardEffect.getLastMessageSent().remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_") || !event.hasItem() || !BARD_CLICK_EFFECTS.containsKey(event.getItem().getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this) || !energy.containsKey(event.getPlayer().getName())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Bard effects cannot be used while in spawn.");
            return;
        }

        if (HCF.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
            return;
        }

        if (getLastEffectUsage().containsKey(event.getPlayer().getName()) && getLastEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            long millisLeft = getLastEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();

            double value = (millisLeft / 1000D);
            double sec = Math.round(10.0 * value) / 10.0;

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            return;
        }

        BardEffect bardEffect = BARD_CLICK_EFFECTS.get(event.getItem().getType());

        if (bardEffect.getEnergy() > energy.get(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough energy for this! You need " + bardEffect.getEnergy() + " energy, but you only have " + energy.get(event.getPlayer().getName()).intValue());
            return;
        }

        String effect = "&cStrength II";
        if (event.getItem().getType() == Material.SUGAR) effect = "&bSpeed III";
        if (event.getItem().getType() == Material.SPIDER_EYE) effect = "&0Wither";
        if (event.getItem().getType() == Material.IRON_INGOT) effect = "&9Resistance III";
        if (event.getItem().getType() == Material.GHAST_TEAR) effect = "&dRegeneration III";
        if (event.getItem().getType() == Material.MAGMA_CREAM) effect = "&bFire Resistance I";
        if (event.getItem().getType() == Material.FEATHER) effect = "&fJump Boost VII";

        int teammates = getNearbyPlayers(event.getPlayer(), true).size();
        if (teammates == 1) {
            event.getPlayer().sendMessage(CC.translate("&eYou have given " + effect + "&e to yourself."));
        } else {
            event.getPlayer().sendMessage(CC.translate("&eYou have given " + effect + "&e to &a" + teammates + " &eteammates."));
        }

        energy.put(event.getPlayer().getName(), energy.get(event.getPlayer().getName()) - bardEffect.getEnergy());

        boolean negative = bardEffect.getPotionEffect() != null && FoxListener.DEBUFFS.contains(bardEffect.getPotionEffect().getType());

        getLastEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + EFFECT_COOLDOWN);
        SpawnTagHandler.addOffensiveSeconds(event.getPlayer(), SpawnTagHandler.getMaxTagTime());
        giveBardEffect(event.getPlayer(), bardEffect, !negative, true);

        if (event.getPlayer().getItemInHand().getAmount() == 1) {
            event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
            event.getPlayer().updateInventory();
        } else {
            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
        }
    }

    public void giveBardEffect(Player source, BardEffect bardEffect, boolean friendly, boolean persistOldValues) {
        for (Player player : getNearbyPlayers(source, friendly)) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                continue;
            }

            // CUSTOM
            // Bards can't get Strength.
            // Yes, that does need to use .equals. PotionEffectType is NOT an enum.
            if (PvPClassHandler.hasKitOn(player, this) && bardEffect.getPotionEffect() != null && bardEffect.getPotionEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                continue;
            }

            if (bardEffect.getPotionEffect() != null) {
                for (PotionEffect peffect : player.getActivePotionEffects()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.addPotionEffect(peffect);
                        }
                    }.runTaskLater(HCF.getInstance(), bardEffect.getPotionEffect().getDuration() + 2);
                }
                smartAddPotion(player, bardEffect.getPotionEffect());
            } else {
                Material material = source.getItemInHand().getType();
                giveCustomBardEffect(player, material);
            }
        }
    }

    public void giveCustomBardEffect(Player player, Material material) {
        switch (material) {
            case FERMENTED_SPIDER_EYE:
                break;
            default:
                HCF.getInstance().getLogger().warning("No custom Bard effect defined for " + material + "");
        }
    }

    public static List<Player> getNearbyPlayers(Player player, boolean friendly) {
        List<Player> valid = new ArrayList<>();
        Team sourceTeam = HCF.getInstance().getTeamHandler().getTeam(player);

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(BARD_RANGE, BARD_RANGE / 2, BARD_RANGE)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (HCF.getInstance().getPvPTimerMap().hasTimer(nearbyPlayer.getUniqueId())) {
                    continue;
                }

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }
                    continue;
                }

                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());
                boolean isAlly = sourceTeam.isAlly(nearbyPlayer.getUniqueId());

                if (friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && isAlly) {
                    if (!HCF.getInstance().getMapHandler().isKitMap())
                        break;
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isFriendly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }
}