package rip.orbit.hcteams.profile;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/07/2021 / 2:08 AM
 * HCTeams / rip.orbit.hcteams.profile
 */
public class ProfileListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (Profile.profileMap.containsKey(event.getPlayer().getUniqueId()))
			return;

		new Profile(event.getPlayer().getUniqueId());

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHit(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			if (event.getEntity() instanceof Player) {
				Profile p = Profile.byUUID(event.getDamager().getUniqueId());
				Profile damaged = Profile.byUUID(event.getEntity().getUniqueId());
				if (!p.getLastHitName().equals(((Player) event.getEntity()).getName())) {
					p.setLastHitName(((Player) event.getEntity()).getName());
				}
				if (!damaged.getLastDamagerName().equals(((Player) event.getDamager()).getName())) {
					damaged.setLastDamagerName(((Player) event.getDamager()).getName());
				}
			}
		}
	}

}
