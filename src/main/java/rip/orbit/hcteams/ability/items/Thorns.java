package rip.orbit.hcteams.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.profile.Profile;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 29/07/2021 / 8:24 AM
 * HCTeams / rip.orbit.hcteams.ability.items
 */
public class Thorns extends Ability {

	public Cooldowns cd = new Cooldowns();
	public static Set<UUID> thorned = new HashSet<>();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public List<String> lore() {
		return CC.translate(Arrays.asList(
				" ",
				"&7Upon right clicking this item the person that last",
				"&7hit you will be inflicted with recoil whenever he",
				"&7hits anyone for 12 seconds.",
				"",
				"&c&lNOTE&7: You must have a block above your head to use this item.",
				" "
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

	@Override
	public String displayName() {
		return CC.chat("&e&lThorns Ability");
	}

	@Override
	public String name() {
		return "thorns";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.IRON_INGOT;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getItem() == null)
			return;
		if (event.getAction().name().contains("RIGHT")) {
			if (isSimilar(event.getItem())) {
				if (!canUse(player)) {
					return;
				}

				Profile profile = Profile.byUUID(player.getUniqueId());
				if (profile.getLastDamagerName().isEmpty() || profile.getLastDamagerName().equalsIgnoreCase("")) {
					player.sendMessage(CC.translate("&cCould not use " + displayName() + " &cbecause there's no last hit."));
					return;
				}
				Player target = Bukkit.getPlayer(profile.getLastDamagerName());
				if (target == null) {
					player.sendMessage(CC.translate("&cCould not use " + displayName() + " &cbecause your last hit has logged off."));
					return;
				}
				Location loc = event.getPlayer().getLocation().clone();
				if (!loc.add(0, 2, 0).getBlock().getType().isSolid()) {
					player.sendMessage(CC.translate("&cYou must have a block above your head to use the " + displayName() + "&c."));
					return;
				}

				thorned.add(target.getUniqueId());

				List<String> hitMsg = Arrays.asList(
						"",
						"&eYou" + " &fhave just used a " + displayName(),
						" ",
						"&7┃ &e" + target.getName() + "&f will now be damaged",
						"&7┃ &fanytime they hit a player for &e12 seconds&f.",
						"");

				List<String> beenHitMsg = Arrays.asList(
						"",
						"&e" + player.getName() + " &fhas just used a " + displayName(),
						" ",
						"&7┃ &eYou" + "&f will now be damaged anytime",
						"&7┃ &fyou hit a player for &e12 seconds&f.",
						"");

				beenHitMsg.forEach(s -> {
					target.sendMessage(CC.translate(s));
				});

				hitMsg.forEach(s -> {
					player.sendMessage(CC.translate(s));
				});

				addCooldown(player, 60);
				event.setCancelled(true);
				takeItem(player);

				new BukkitRunnable() {
					@Override
					public void run() {
						thorned.remove(target.getUniqueId());
					}
				}.runTaskLater(HCF.getInstance(), 20 * 12);

			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (thorned.contains(event.getDamager().getUniqueId())) {
			if (event.getDamager() instanceof Player) {
				Player damager = (Player) event.getDamager();
				double damageToInflint = event.getDamage() * 0.35D;
				damager.damage(damageToInflint);
			}
		}
	}

}
