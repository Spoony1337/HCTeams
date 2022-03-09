package rip.orbit.hcteams.deathmessage.trackers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import rip.orbit.hcteams.deathmessage.event.CustomPlayerDamageEvent;
import rip.orbit.hcteams.deathmessage.objects.Damage;

public class GeneralTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        switch (event.getCause().getCause()) {
            case SUFFOCATION:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "suffocated"));
                break;
            case DROWNING:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "drowned"));
                break;
            case STARVATION:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "starved to death"));
                break;
            case LIGHTNING:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "was struck by lightning"));
                break;
            case POISON:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "was poisoned"));
                break;
            case WITHER:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "withered away"));
                break;
            default:
                break;
        }
    }

    public static class GeneralDamage extends Damage {

        private String message;

        public GeneralDamage(String damaged, double damage, String message) {
            super(damaged, damage);
            this.message = message;
        }

        @Override
		public String getDeathMessage() {
            return (wrapName(getDamaged()) + " " + message + ".");
        }

    }

}