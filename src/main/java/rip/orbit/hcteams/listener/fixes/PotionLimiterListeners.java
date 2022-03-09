package rip.orbit.hcteams.listener.fixes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;

public class PotionLimiterListeners implements Listener {

    public boolean isBlocked(int data) {
        return HCF.getInstance().getConfig().getStringList("BLOCKED_POTIONS").contains(data + "");
    }
    
    @EventHandler
    public void onPotionSplashEvent(PotionSplashEvent event) {
        ItemStack itemStack = event.getPotion().getItem();
        if (isBlocked(itemStack.getDurability())) {

            if (event.getPotion().getShooter() instanceof Player) {
                Player player = (Player) event.getPotion().getShooter();
                player.updateInventory();
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().getType() == Material.POTION) {
            if (isBlocked(event.getItem().getDurability())) {
                player.sendMessage(HCF.getInstance().getConfig().getString("POTION_LIMITER.BLOCKED"));
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

}
