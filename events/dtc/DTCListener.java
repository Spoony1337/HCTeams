package rip.orbit.hcteams.events.dtc;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventType;
import rip.orbit.hcteams.team.dtr.DTRBitmask;

public class DTCListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST) // so people don't get their chat spammed
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Location location = event.getBlock().getLocation();
        if (!DTRBitmask.DTC.appliesAt(location)) {
            return;
        }
        
        DTC activeDTC = (DTC) HCF.getInstance().getEventHandler().getEvents().stream().filter(Event::isActive).filter(ev -> ev.getType() == EventType.DTC).findFirst().orElse(null);
        
        if (activeDTC == null) {
            return;
        }

        if (event.getBlock().getType() == Material.OBSIDIAN) {
            event.setCancelled(true);
            activeDTC.blockBroken(event.getPlayer());
        }
    }
    
}
