package rip.orbit.hcteams.listener.kits;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import rip.orbit.hcteams.HCF;

public class RefillSignCreateListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event){
        if ((HCF.getInstance().getMapHandler().isKitMap()) && event.getLine(2).equalsIgnoreCase(ChatColor.DARK_RED + "- Refill -")){
            Player player = event.getPlayer();
            if (!player.hasPermission("refillsign.create")){
                player.sendMessage(ChatColor.RED + "No permission.");
                event.setCancelled(true);
            }
        }
    }
}
