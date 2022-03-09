package rip.orbit.hcteams.ability.items.halloween;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.profile.Profile;
import rip.orbit.hcteams.pvpclasses.PvPClass;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.JavaUtils;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/09/2021 / 8:29 PM
 * HCTeams / rip.orbit.hcteams.ability.items.halloween
 */
public class PeekaBoo extends Ability {

	public Cooldowns cd = new Cooldowns();
	public ConcurrentHashMap<UUID, Integer> hits = new ConcurrentHashMap<>();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public String name() {
		return "peekaboo";
	}

	@Override
	public String displayName() {
		return "&6&lPeek-a-Boo";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.INK_SACK;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@Override
	public List<String> lore() {
		return CC.translate(Arrays.asList(
				"",
				"&7Hit a player 3 times to be teleported",
				"&7behind them and be given Strength II for",
				"&76 seconds.",
				"",
				"&c&lNOTE&7: They receive blindness for 10 seconds",
				""
		));
	}

	@Override
	public List<String> foundInfo() {
		return Arrays.asList(
				"Ability Packages",
				"Partner Crates",
				"Star Shop (/starshop)"
		);
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if (!checkInstancePlayer(event.getEntity()))
			return;
		if (!checkInstancePlayer(event.getDamager()))
			return;

		Player damager = (Player) event.getDamager();
		Profile profile = Profile.byUUID(damager.getUniqueId());

		if (!isSimilar(damager.getItemInHand()))
			return;

		Player damaged = (Player) event.getEntity();

		if (!canUse(damager)) {
			return;
		}
		if (!canAttack(damager, damaged))
			return;

		if (!hits.isEmpty() && hits.get(damager.getUniqueId()) != null && hits.get(damager.getUniqueId()) >= 3) {

			List<String> hitMsg = Arrays.asList(
					"",
					"&6&lYOU HAVE HIT SOMEONE!",
					" ",
					"&6You" + " &fhave just hit &6" + damaged.getName(),
					"&fwith a &6Peek-a-Boo&f.",
					" ",
					"&7┃ &fThey are now blinded",
					"&7┃ &ffor &610 seconds&f.",
					"");

			hitMsg.forEach(s -> damager.sendMessage(CC.chat(s)));

			HCF.getInstance().getAbilityHandler().getAbilityEffect().applyCooldown(damaged, 15);

			addCooldown(damager, 90);
			takeItem(damager);

			CompletableFuture.runAsync(() -> hits.remove(damager.getUniqueId()));

			damaged.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 5));
			PvPClass.setRestoreEffect(damager, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (20 * 6) + 10, 1));

			Vector inverseDirectionVec = damaged.getLocation().getDirection().normalize().multiply(-1.5);
			inverseDirectionVec.setY(inverseDirectionVec.getY());

			damager.teleport(new Location(damager.getWorld(), inverseDirectionVec.getX(), inverseDirectionVec.getY(), inverseDirectionVec.getZ()));

			return;
		}

		if (profile.canHit(profile.getCurseHitTime())) {
			CompletableFuture.runAsync(() -> hits.remove(damager.getUniqueId()));
		}

		addHits(damager, hits);

		profile.setPeekABooHitTime(System.currentTimeMillis() + JavaUtils.parse("15s"));
	}

}
