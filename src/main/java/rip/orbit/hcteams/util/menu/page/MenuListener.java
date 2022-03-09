package rip.orbit.hcteams.util.menu.page;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import rip.orbit.hcteams.util.AbstractMenu;


public class MenuListener implements Listener
{
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractMenu) {
            ((AbstractMenu)event.getInventory().getHolder()).onInventoryClick(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractMenu) {
            ((AbstractMenu)event.getInventory().getHolder()).onInventoryDrag(event);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractMenu) {
            ((AbstractMenu)event.getInventory().getHolder()).onInventoryClose(event);
        }
    }

}
