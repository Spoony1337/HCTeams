package rip.orbit.hcteams.events.killtheking;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;
import rip.orbit.hcteams.server.ServerHandler;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.TaskUtil;


public class KingEventListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        if (KingEvent.isStarted(false) && KingEvent.getFocusedPlayer() == player) {
            event.getDrops().clear();
            Location location = player.getLocation();
            if (killer != null && !player.equals(killer)) {

                Bukkit.getOnlinePlayers().forEach(online -> online.sendMessage(KingEvent.getWinnerAlert(killer.getName())));
                KingEvent.clean();
            } else {
                Bukkit.broadcastMessage(CC.translate("&4&l[KTK EVENT] &a" + player.getName() + " &edied, but will be respawned and teleported to the last location."));
                TaskUtil.runLater(() -> {
                    player.spigot().respawn();
                    player.teleport(location);
                    SpawnTagHandler.addOffensiveSeconds(player, 300 * 3);
                }, 5L);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (KingEvent.isStarted(true) && KingEvent.getFocusedPlayer() == player) {
            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setArmorContents(null);
            TaskUtil.runLater(() -> player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType())), 5L);
            TaskUtil.run(KingEvent::clean);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (get(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (get(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (get((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Player player = event.getPlayer();
        if (get(player)) {
            Location from = event.getFrom(), to = event.getTo();
            if (!isWithinWarZone(to)) {
                player.teleport(from);
                player.setSprinting(false);
                if (player.getVehicle() != null) {
                    player.getVehicle().remove();
                }
                player.setVelocity(to.toVector().subtract(player.getLocation().toVector()).normalize().multiply(-1));
            }
        }
    }

    private boolean isWithinWarZone(Location to) {
        Location location = to.getWorld().getSpawnLocation();
        return location.distance(to) <= ServerHandler.WARZONE_RADIUS;
    }

    private boolean get(Player player) {
        return KingEvent.isStarted(true) && KingEvent.getFocusedPlayer() == player;
    }
}