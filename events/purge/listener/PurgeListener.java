package rip.orbit.hcteams.events.purge.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import rip.orbit.hcteams.events.purge.commands.PurgeCommands;


public class PurgeListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerBuild(BlockBreakEvent event) {
        if (PurgeCommands.isPurgeTimer()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (PurgeCommands.isPurgeTimer() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material m = event.getClickedBlock().getType(); //get the block type clicked
            event.setCancelled(!m.equals(Material.CHEST) || !m.equals(Material.FENCE_GATE));
        }
    }
}
