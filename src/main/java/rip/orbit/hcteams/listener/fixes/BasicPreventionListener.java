package rip.orbit.hcteams.listener.fixes;

import net.frozenorb.qlib.qLib;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;

public class BasicPreventionListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!event.getPlayer().hasPermission("foxtrot.coloredsign")) return;
        for (int i = 0; i < 3; i++) {
            event.setLine(i, CC.chat(event.getLine(i)));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerKickEvent event) {
        event.setLeaveMessage(null);
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Wither) {
            event.setCancelled(true);
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            if (event.getEntity() instanceof Creeper) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof Blaze) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof Skeleton) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof Ghast) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof CaveSpider) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof MagmaCube) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof Silverfish) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof PigZombie) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof Slime) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof Witch) {
                event.setCancelled(true);
            }
            if (event.getEntity() instanceof Zombie) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

    /*    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getGameMode() != GameMode.CREATIVE) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
            }
        }*/
    }

  /*  @EventHandler(priority=EventPriority.HIGH)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST && !ChestCommand.getBYPASS().contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }*/

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/kill")
                || event.getMessage().toLowerCase().startsWith("/slay")
                || event.getMessage().toLowerCase().startsWith("/bukkit:kill")
                || event.getMessage().toLowerCase().startsWith("/bukkit:slay")
                || event.getMessage().toLowerCase().startsWith("/suicide")) {
            if (!event.getPlayer().isOp()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Horse && event.getEntered() instanceof Player) {
            Horse horse = (Horse) event.getVehicle();
            Player player = (Player) event.getEntered();

            if (horse.getOwner() != null && !horse.getOwner().getName().equals(player.getName())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "This is not your horse!");
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation()) && event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
            event.setCancelled(true);
            return;
        }

        if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
            // Make food drop 1/2 as fast if you have PvP protection
            if (qLib.RANDOM.nextInt(100) > (HCF.getInstance().getPvPTimerMap().hasTimer(event.getEntity().getUniqueId()) ? 10 : 30)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!HCF.getInstance().getInDuelPredicate().test(event.getPlayer())) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(HCF.getInstance(), new Runnable() {
                
                @Override
				public void run() {
                    HCF.getInstance().getPvPTimerMap().createTimer(event.getPlayer().getUniqueId(), 30 * 60);//moved inside here due to occasional CME maybe this will fix?
                }
            }, 20L);
        }
        event.setRespawnLocation(HCF.getInstance().getServerHandler().getSpawnLocation());
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (HCF.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (HCF.getInstance().getServerHandler().isSkybridgePrevention()
                && 110 < event.getBlock().getLocation().getY()
                && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't build higher than 110 blocks.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFish(PlayerInteractEvent event) {
        if (!HCF.getInstance().getServerHandler().isRodPrevention()
                || (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.FISHING_ROD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && event.getEntity().getType() == EntityType.SKELETON && ((Skeleton) event.getEntity()).getSkeletonType() == Skeleton.SkeletonType.WITHER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER && (event.getBlock().getType() == Material.BED
                || event.getBlock().getType() == Material.BED_BLOCK)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place beds in the Nether.");
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireBurn(BlockBurnEvent event) {
        if (HCF.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
            event.setCancelled(true);
            return;
        }

        if (HCF.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        EntityType type = event.getEntityType();
        if (type == EntityType.MINECART_TNT) {
            event.setCancelled(true);
        }
    }

}
