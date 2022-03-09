package rip.orbit.hcteams.pvpclasses;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.pvpclasses.event.BardRestoreEvent;
import rip.orbit.hcteams.pvpclasses.pvpclasses.ArcherClass;
import rip.orbit.hcteams.pvpclasses.pvpclasses.BardClass;
import rip.orbit.hcteams.pvpclasses.pvpclasses.MinerClass;
import rip.orbit.hcteams.pvpclasses.pvpclasses.RogueClass;

import java.util.*;

public class PvPClassHandler extends BukkitRunnable implements Listener {

    @Getter private static Map<String, PvPClass> equippedKits = new HashMap<>();

    @Getter private static Map<UUID, PvPClass.SavedPotion> savedPotions = new HashMap<>();
    @Getter List<PvPClass> pvpClasses = new ArrayList<>();

    public PvPClassHandler() {
        pvpClasses.add(new MinerClass());

        if (HCF.getInstance().getConfig().getBoolean("pvpClasses.archer", true)) {
            pvpClasses.add(new ArcherClass());
        }
//        if (HCF.getInstance().getConfig().getBoolean("pvpClasses.mage", true)) {
//            pvpClasses.add(new MageClass());
//        }

//        if (HCF.getInstance().getConfig().getBoolean("pvpClasses.ranger", true)) {
//            pvpClasses.add(new RangerClass());
//        }

        if (HCF.getInstance().getConfig().getBoolean("pvpClasses.bard", true)) {
            pvpClasses.add(new BardClass());
        }

        if (HCF.getInstance().getConfig().getBoolean("pvpClasses.rogue", true)) {
            pvpClasses.add(new RogueClass());
        }

//        if (HCF.getInstance().getConfig().getBoolean("pvpClasses.scout", false)) {
//            pvpClasses.add(new ScoutClass());
//        }

        for (PvPClass pvpClass : pvpClasses) {
            HCF.getInstance().getServer().getPluginManager().registerEvents(pvpClass, HCF.getInstance());
        }

        HCF.getInstance().getServer().getScheduler().runTaskTimer(HCF.getInstance(), this, 2L, 2L);
        HCF.getInstance().getServer().getPluginManager().registerEvents(this, HCF.getInstance());
    }


    @Override
	public void run() {
        for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
            // Remove kit if player took off armor, otherwise .tick();
            if (equippedKits.containsKey(player.getName())) {
                PvPClass equippedPvPClass = equippedKits.get(player.getName());

                if (!equippedPvPClass.qualifies(player.getInventory())) {
                    equippedKits.remove(player.getName());
                    player.sendMessage(ChatColor.RED + "You have disabled your " + equippedPvPClass.getName() + " Class!");
                    //player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + equippedPvPClass.getName() + ChatColor.GRAY + " --> " + ChatColor.RED + "Disabled!");
                    equippedPvPClass.remove(player);
                    PvPClass.removeInfiniteEffects(player);
                } else if (!player.hasMetadata("frozen")) {
                    equippedPvPClass.tick(player);
                }
            } else {
                // Start kit warmup
                for (PvPClass pvpClass : pvpClasses) {
                    if (pvpClass.qualifies(player.getInventory()) && pvpClass.canApply(player) && !player.hasMetadata("frozen")) {
                        pvpClass.apply(player);
                        PvPClassHandler.getEquippedKits().put(player.getName(), pvpClass);

                        player.sendMessage(ChatColor.GREEN + "You have enabled your " + pvpClass.getName() + " Class!");
                        //player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + pvpClass.getName() + ChatColor.GRAY + " --> " + ChatColor.GREEN + "Enabled!");
                        //player.sendMessage(ChatColor.AQUA + "Class Info: " + ChatColor.GREEN + pvpClass.getSiteLink());
                        break;
                    }
                }
            }
        }
        checkSavedPotions();
    }

    public void checkSavedPotions() {
        Iterator<Map.Entry<UUID, PvPClass.SavedPotion>> idIterator = savedPotions.entrySet().iterator();
        while (idIterator.hasNext()) {
            Map.Entry<UUID, PvPClass.SavedPotion> id = idIterator.next();
            Player player = Bukkit.getPlayer(id.getKey());
            if (player != null && player.isOnline()) {
                Bukkit.getPluginManager().callEvent(new BardRestoreEvent(player, id.getValue()));
                if (id.getValue().getTime() < System.currentTimeMillis() && !id.getValue().isPerm()) {
                    if (player.hasPotionEffect(id.getValue().getPotionEffect().getType())) {
                        player.getActivePotionEffects().forEach(potion -> {
                            PotionEffect restore = id.getValue().getPotionEffect();
                            if (potion.getType() == restore.getType() && potion.getDuration() < restore.getDuration() && potion.getAmplifier() <= restore.getAmplifier()) {
                                player.removePotionEffect(restore.getType());
                            }
                        });
                    }
                    
                    if (player.addPotionEffect(id.getValue().getPotionEffect(), true)) {
                        Bukkit.getLogger().info(id.getValue().getPotionEffect().getType() + ", " + id.getValue().getPotionEffect().getDuration() + ", " + id.getValue().getPotionEffect().getAmplifier());
                        idIterator.remove();
                    }
                }
            } else {
                idIterator.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand() == null || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        for (PvPClass pvPClass : pvpClasses) {
            if (hasKitOn(event.getPlayer(), pvPClass) && pvPClass.getConsumables() != null && pvPClass.getConsumables().contains(event.getPlayer().getItemInHand().getType())) {
                if (pvPClass.itemConsumed(event.getPlayer(), event.getItem().getType())) {
                    if (event.getPlayer().getItemInHand().getAmount() > 1) {
                        event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                    } else {
                        event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());
                        //event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    public static PvPClass getPvPClass(Player player) {
        return (equippedKits.getOrDefault(player.getName(), null));
    }

    public static boolean hasKitOn(Player player, PvPClass pvpClass) {
        return (equippedKits.containsKey(player.getName()) && equippedKits.get(player.getName()) == pvpClass);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }

        for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1_000_000) {
                event.getPlayer().removePotionEffect(potionEffect.getType());
            }
        }
    }

}