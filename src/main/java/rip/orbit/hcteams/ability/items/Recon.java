package rip.orbit.hcteams.ability.items;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.ability.event.ReconPlayerFoundEvent;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 10/07/2021 / 11:58 PM
 * HCTeams / rip.orbit.hcteams.ability.items
 */
public class Recon extends Ability {

	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public String name() {
		return "recon";
	}

	@Override
	public String displayName() {
		return CC.chat("&d&lRecon Ability");
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.COMPASS;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@Override
	public List<String> lore() {
		return CC.translate(Arrays.asList(
				"",
				"&7Click this to search for players in a 15x15",
				"&7radius of your location. If there's any players",
				"&7found their names and locations will be revealed",
				"&7to you. They will be notified that they have been",
				"&7found. They will also receive blindness and slowness",
				"&7for 5 seconds.",
				"",
				"&c&lNOTE&7: If you're on CB/LC Client a Waypoint marks their location.",
				""
		));
	}

	@Override
	public List<String> foundInfo() {
		return CC.translate(Arrays.asList(
				"Ability Packages",
				"Partner Crates",
				"Star Shop (/starshop)"
		));
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (isSimilar(event)) {
			if (!isClick(event, "RIGHT")) {
				event.setCancelled(true);
				return;
			}
			if (!canUse(p)) {
				event.setCancelled(true);
				return;
			}
			event.setCancelled(true);

			addCooldown(p, 75);
			takeItem(p);

			LCWaypoint waypoint = null;

			List<Player> foundNames = new ArrayList<>();
			for (Entity nearbyEntity : p.getNearbyEntities(15, 15, 15)) {
				if (nearbyEntity instanceof Player) {
					Player found = (Player) nearbyEntity;
					ReconPlayerFoundEvent playerFoundEvent = new ReconPlayerFoundEvent(p, found);
					Bukkit.getPluginManager().callEvent(playerFoundEvent);

					found.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1));
					found.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));

					foundNames.add(found);
					waypoint = new LCWaypoint(found.getName() + "'s Location", found.getLocation(), Color.LIGHT_GRAY.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(p, waypoint);
				}
			}
			if (!foundNames.isEmpty()) {
				LCWaypoint finalWaypoint = waypoint;
				new BukkitRunnable() {
					@Override
					public void run() {
						LunarClientAPI.getInstance().removeWaypoint(p, finalWaypoint);
					}
				}.runTaskLater(HCF.getInstance(), 20 * 15);
			}
			if (foundNames.isEmpty()) {
				p.sendMessage(CC.chat("&d&lPlayer's Found"));
				foundNames.forEach(s -> {
					p.sendMessage(CC.chat("&7» &dNone"));
				});
			} else {
				p.sendMessage(CC.chat("&d&lPlayer's Found"));
				foundNames.forEach(s -> {
					int x = s.getLocation().getBlockX();
					int y = s.getLocation().getBlockY();
					int z = s.getLocation().getBlockZ();
					p.sendMessage(CC.chat("&7» &d" + s.getName() + " &7(" + x + ", " + y + ", " + z + ")"));
				});
			}
			p.sendMessage(" ");
			p.sendMessage(CC.chat("&7&oIf you're on LC/CB the Waypoints will be deleted in 15 seconds."));
		}
	}

	@EventHandler
	public void onPlayerFoundEvent(ReconPlayerFoundEvent event) {
		event.getPlayer().sendMessage(CC.chat("&c" + event.getUser().getName() + " has just used a " + displayName() + " &cand your location is now visible to them."));
	}

}
