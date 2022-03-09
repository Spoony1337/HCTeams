package rip.orbit.hcteams.listener.fixes;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedstoneListener implements Listener {

    @Getter
    private Map<UUID, Long> leverCooldown = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getClickedBlock() == null
                || event.getClickedBlock().getType() != Material.LEVER) return;
        Player player = event.getPlayer();

        if (leverCooldown.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
            event.setCancelled(true);
            return;
        }

        leverCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 500L);
    }
//
//    @EventHandler
//    public void onRedstone(BlockRedstoneEvent event) {
//
//        double tps1m = Bukkit.spigot().getTPS()[0];
//
//        if (tps > 14 || tps1m > 19) return;
//
//        event.setNewCurrent(0);
//
//        if (tps <= 10) {
//            Bukkit.getLogger().info("Lag spike detected, attempting to fix via removing redstone..");
//            event.getBlock().setType(Material.AIR);
//            Bukkit.getScheduler().runTaskLaterAsynchronously(HCF.getInstance(), () -> event.getBlock().setType(Material.AIR), 1);
//        }

}