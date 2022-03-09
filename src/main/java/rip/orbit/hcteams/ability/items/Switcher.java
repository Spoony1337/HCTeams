package rip.orbit.hcteams.ability.items;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/07/2021 / 12:48 AM
 * HCTeams / rip.orbit.hcteams.ability.items
 */
public class Switcher extends Ability {

	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public String name() {
		return "switcher";
	}

	@Override
	public String displayName() {
		return CC.chat("&a&lSwitcher");
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.SNOW_BALL;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@Override
	public List<String> lore() {
		return CC.translate(Arrays.asList(
				" ",
				"&7Throw this and if it hits a player your",
				"&7locations will be swapped.",
				" ",
				"&c&lNOTE&7: This can only be done if they're at least 10 blocks away.",
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

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (isSimilar(event)) {
			if (!isClick(event, "RIGHT")) {
				event.setUseItemInHand(Event.Result.DENY);
				return;
			}
			if (!canUse(p)) {
				event.setUseItemInHand(Event.Result.DENY);
				return;
			}
			event.setUseItemInHand(Event.Result.DENY);

			addCooldown(p, 15);
			Snowball snowball = p.launchProjectile(Snowball.class);
			snowball.setMetadata("switcher", new FixedMetadataValue(HCF.getInstance(), true));
			takeItem(p);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSwitcherHit(EntityDamageByEntityEvent event) {
		if (checkInstancePlayer(event.getEntity())) {
			if (checkInstanceSnowball(event.getDamager())) {
				if (event.getDamager().hasMetadata("switcher")) {
					if (checkInstancePlayer(((Snowball) event.getDamager()).getShooter())) {
						Player shooter = (Player) ((Snowball) event.getDamager()).getShooter();
						Player damaged = (Player) event.getEntity();
						if (!canAttack(shooter, damaged))
							return;
						Location damagedLoc = damaged.getLocation();
						if (!isInDistance(shooter, damaged, 10)) {
							shooter.sendMessage(CC.chat("&cThat player was out of range."));
							return;
						}
						damaged.teleport(shooter);
						shooter.sendMessage(CC.chat("&aYou have just swapped locations with " + damaged.getName()));
						damaged.sendMessage(CC.chat("&aYour location has just swapped with " + shooter.getName()));
						shooter.teleport(damagedLoc);
					}
				}
			}
		}
	}

}
