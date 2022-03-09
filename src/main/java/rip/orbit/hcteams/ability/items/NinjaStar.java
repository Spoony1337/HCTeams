package rip.orbit.hcteams.ability.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/07/2021 / 5:23 PM
 * HCTeams / rip.orbit.hcteams.ability.items
 */
public class NinjaStar extends Ability {

	public Cooldowns cd = new Cooldowns();
	public static final ConcurrentHashMap<UUID, UUID> lastHitMap = new ConcurrentHashMap<>();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public String name() {
		return "ninjastar";
	}

	@Override
	public String displayName() {
		return CC.chat("&a&lNinja Star");
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.NETHER_STAR;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@Override
	public List<String> lore() {
		return CC.translate(
				Arrays.asList(
						" ",
						"&7Right click this to teleport to start a 3 second sequence",
						"&7where if you're hit within the last 15 seconds, whoever",
						"&7hit you last will be the person you teleport to after the",
						"&73 seconds is up.",
						" ",
						"&c&lNOTE&7: The cooldown on this is really long, so use it wisely.",
						" "
				)
		);
	}

	@Override
	public List<String> foundInfo() {
		return CC.translate(Arrays.asList(
				"Autumn Crates",
				"Star Shop (/starshop)"
		));
	}

	@EventHandler
	public void onInteractNinjaStar(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (isSimilar(event.getItem())) {
			if (!isClick(event, "RIGHT")) {
				event.setUseItemInHand(Event.Result.DENY);
				return;
			}
			if (!canUse(player)) {
				event.setUseItemInHand(Event.Result.DENY);
				return;
			}
			if (lastHitMap.get(player.getUniqueId()) == null || !lastHitMap.containsKey(player.getUniqueId())) {
				player.sendMessage(CC.chat("&a&lNinjaStar &7» &fThere is no last hit"));
				return;
			}
			Player target = Bukkit.getPlayer(lastHitMap.get(player.getUniqueId()));
			if (target == null) {
				player.sendMessage(CC.chat("&a&lNinjaStar &7» &fThere is no last hit"));
				return;
			}

			addCooldown(player, 150);
			event.setCancelled(true);

			List<String> hitMsg = Arrays.asList(
					"",
					"&aYou" + " &fhave just used a " + displayName(),
					" ",
					"&7┃ &fYou will be teleported to &a%player%&f in &a3 seconds&f.",
					"");

			hitMsg.forEach(s -> {
				player.sendMessage(CC.translate(s.replaceAll("%player%", target.getName())));
			});

			new BukkitRunnable() {
				int i = 3;
				@Override
				public void run() {
					if (i == 0) {
						cancel();
						target.sendMessage(CC.chat("&a&lNinjaStar &7» &a" + player.getName() + " &fhas been teleported to you."));
						player.sendMessage(CC.chat("&a&lNinjaStar &7» &aYou &fhave been teleported to &a" + target.getName()));
						player.teleport(target);
						return;
					}
					if (i == 1) {
						target.sendMessage(CC.chat("&a&lNinjaStar &7» &a" + player.getName() + " &fwill be teleported to you in &a" + i + " second"));
						player.sendMessage(CC.chat("&a&lNinjaStar &7» &aYou &fwill be teleported to &a" + target.getName() + " &fin &a" + i + " second"));
					} else {
						target.sendMessage(CC.chat("&a&lNinjaStar &7» &a" + player.getName() + " &fwill be teleported to you in &a" + i + " seconds"));
						player.sendMessage(CC.chat("&a&lNinjaStar &7» &aYou &fwill be teleported to &a" + target.getName() + " &fin &a" + i + " seconds"));
					}
					--i;
				}
			}.runTaskTimer(HCF.getInstance(), 20, 20);
		}
	}

}
