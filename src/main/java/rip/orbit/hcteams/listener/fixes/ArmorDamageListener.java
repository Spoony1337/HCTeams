package rip.orbit.hcteams.listener.fixes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.Random;

public class ArmorDamageListener implements Listener {

    private static Random random = new Random();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onItemDamage(PlayerItemDamageEvent e) {

        if (random.nextInt(100) < 1) {
            e.setDamage(1);
        } else {
            e.setDamage(0);
        }
        
    }
}
