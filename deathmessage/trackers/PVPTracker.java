package rip.orbit.hcteams.deathmessage.trackers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.deathmessage.event.CustomPlayerDamageEvent;
import rip.orbit.hcteams.deathmessage.objects.PlayerDamage;
import rip.orbit.hcteams.deathmessage.util.MobUtil;

public class PVPTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();

            if (e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player damaged = event.getPlayer();

                event.setTrackerDamage(new PVPDamage(damaged.getName(), event.getDamage(), damager.getName(), damager.getItemInHand()));
            }
        }
    }

    public static class PVPDamage extends PlayerDamage {

        private String itemString;

        public PVPDamage(String damaged, double damage, String damager, ItemStack itemStack) {
            super(damaged, damage, damager);
            this.itemString = "Error";

            if (itemStack.getType() == Material.AIR) {
                itemString = "their fists";
            } else {
                itemString = MobUtil.getItemName(itemStack);
            }
        }

        @Override
		public String getDeathMessage() {
            String extension = (HCF.getInstance().getInDuelPredicate().test(Bukkit.getPlayer(getDamaged()))) ?
                    " during a duel."
                    :
                    " using " + ChatColor.RED + itemString + ChatColor.YELLOW + ".";
            return (wrapName(getDamaged()) + " was slain by " + wrapName(getDamager()) + extension);
        }

    }

}