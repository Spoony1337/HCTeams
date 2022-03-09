package rip.orbit.hcteams.ability.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class GuardianAngel extends Ability {

	public HashSet<UUID> gAngel = new HashSet<>();

	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public List<String> lore() {
		return CC.translate(Arrays.asList(
				" ",
				"&7After clicking this you will have 15 seconds to",
				"&7go under 2 hearts. If you do go under 2 hearts within",
				"&7that 15 seconds you will be granted full health.",
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
		return CC.chat("&5&lGuardian Angel");
	}

	@Override
	public String name() {
		return "guardianangel";
	}

	@Override
	public int data() {
		return 5;
	}

	@Override
	public Material mat() {
		return Material.INK_SACK;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@EventHandler
	public void onGuardianAngel(PlayerInteractEvent event) {
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
			gAngel.add(player.getUniqueId());
			List<String> hitMsg = Arrays.asList(
					"",
					"&5You" + " &fhave just used a " + displayName(),
					" ",
					"&7┃ &fYou have &515 seconds&f to get below 2 hearts.",
					"&7┃ &fIf this does happen you will be granted full",
					"&7┃ &fhealth.",
					"");

			hitMsg.forEach(s -> player.sendMessage(CC.chat(s)));
			event.setCancelled(true);
			takeItem(player);

			addCooldown(player, 90);

			activateGuardianAngel(player);
		}
	}

	@EventHandler
	public void onPlayerDmg(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			if (gAngel.contains(damaged.getUniqueId())) {
				double health = damaged.getHealth();
				if (health <= 2.0) {
					event.setDamage(0);
					deactivateGuardianAngel(damaged, "hit");

					List<String> hitMsg = Arrays.asList(
							"",
							"&5Your " + displayName() + "&f has just activated.",
							" ",
							"&7┃ &fYou now have &5full&f health.",
							"");

					hitMsg.forEach(s -> damaged.sendMessage(CC.chat(s)));
				}
			}
		}
	}

	private void activateGuardianAngel(Player player) {
		gAngel.add(player.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				if (gAngel.contains(player.getUniqueId())) {
					deactivateGuardianAngel(player, "wornoff");
				}
			}
		}.runTaskLater(HCF.getInstance(), 20 * 30);
	}

	private void deactivateGuardianAngel(Player player, String reason) {
		if (reason.equalsIgnoreCase("hit")) {
			player.setHealth(player.getMaxHealth());
			gAngel.remove(player.getUniqueId());
		} else {
			gAngel.remove(player.getUniqueId());
			player.sendMessage(CC.chat("&cYour guardian angel has worn off and will no longer work!"));
		}
	}
}
