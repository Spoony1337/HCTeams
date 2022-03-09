package rip.orbit.hcteams.ability.items.pocketbard;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/07/2021 / 6:03 PM
 * HCTeams / rip.orbit.hcteams.ability.items.pocketbard
 */
public class Speed extends Ability {

	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public String name() {
		return "speedpocketbard";
	}

	@Override
	public String displayName() {
		return "&a&lSpeed III";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.SUGAR;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@Override
	public List<String> lore() {
		return CC.translate(Arrays.asList(
				"",
				"&7Right click to receive speed 3 for",
				"&75 seconds.",
				"",
				"&c&lNOTE&7: Your teammates receive the effects as well.",
				""
		));
	}

	@Override
	public List<String> foundInfo() {
		return null;
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

			addCooldown(player, 60);
			event.setCancelled(true);
			takeItem(player);

			PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 25 * 5, 2);
			player.removePotionEffect(PotionEffectType.SPEED);
			player.addPotionEffect(effect);

			Team team = HCF.getInstance().getTeamHandler().getTeam(player);

			List<String> hitMsg = Arrays.asList(
					"",
					"&aYou &fhave just activated a " + displayName() + "&f.",
					" ",
					"&7┃ &fYou now have " + displayName() + " &ffor 5 seconds",
					"");

			if (team != null) {
				player.getNearbyEntities(15, 15, 15).forEach(entity -> {
					if (entity instanceof Player) {
						Player p = (Player) entity;
						Team other = HCF.getInstance().getTeamHandler().getTeam(p);
						if (other != null) {
							if (other == team) {
								p.removePotionEffect(PotionEffectType.SPEED);
								p.addPotionEffect(effect);

								hitMsg.forEach(s -> p.sendMessage(CC.chat(s)));
							}
						}
					}
				});

				hitMsg.forEach(s -> player.sendMessage(CC.chat(s)));
			}

		}
	}

}
