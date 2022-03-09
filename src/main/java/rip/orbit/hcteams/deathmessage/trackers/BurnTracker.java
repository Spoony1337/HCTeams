package rip.orbit.hcteams.deathmessage.trackers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import rip.orbit.hcteams.deathmessage.DeathMessageHandler;
import rip.orbit.hcteams.deathmessage.event.CustomPlayerDamageEvent;
import rip.orbit.hcteams.deathmessage.objects.Damage;
import rip.orbit.hcteams.deathmessage.objects.PlayerDamage;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BurnTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.FIRE_TICK && event.getCause().getCause() != EntityDamageEvent.DamageCause.LAVA) {
            return;
        }

        List<Damage> record = DeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;

        if (record != null) {
            for (Damage damage : record) {
                if (damage instanceof BurnDamage || damage instanceof BurnDamageByPlayer) {
                    continue;
                }

                if (damage instanceof PlayerDamage && (knocker == null || damage.getTime() > knockerTime)) {
                    knocker = damage;
                    knockerTime = damage.getTime();
                }
            }
        }

        if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1) > System.currentTimeMillis() ) {
            event.setTrackerDamage(new BurnDamageByPlayer(event.getPlayer().getName(), event.getDamage(), ((PlayerDamage) knocker).getDamager()));
        } else {
            event.setTrackerDamage(new BurnDamage(event.getPlayer().getName(), event.getDamage()));

        }
    }

    public static class BurnDamage extends Damage {

        public BurnDamage(String damaged, double damage) {
            super(damaged, damage);
        }

        @Override
		public String getDeathMessage() {
            return (wrapName(getDamaged()) + " burned to death");
        }

    }

    public static class BurnDamageByPlayer extends PlayerDamage {

        public BurnDamageByPlayer(String damaged, double damage, String damager) {
            super(damaged, damage, damager);
        }

        @Override
		public String getDeathMessage() {
            return (wrapName(getDamaged()) + " burned to death thanks to " + wrapName(getDamager()) + ".");
        }

    }

}