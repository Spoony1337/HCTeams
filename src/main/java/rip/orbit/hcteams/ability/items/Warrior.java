package rip.orbit.hcteams.ability.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.pvpclasses.PvPClass;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/07/2021 / 9:25 AM
 * HCTeams / rip.orbit.hcteams.ability.items
 */
public class Warrior extends Ability {

	public Cooldowns cd = new Cooldowns();
	public Cooldowns warrior = new Cooldowns();
	public ConcurrentHashMap<UUID, Integer> hits = new ConcurrentHashMap<>();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public String name() {
		return "warrior";
	}

	@Override
	public String displayName() {
		return CC.chat("&c&lWarrior Ability");
	}

	@Override
	public int data() {
		return 1;
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
				" ",
				"&7Click this to start a 10 second sequence",
				"&7where as many hits that you get for the next",
				"&710 seconds is how many seconds you will receive",
				"&7Strength II, Resistance III, Regeneration III,",
				"&7, and Speed III",
				" ",
				"&c&lNOTE&7: You can only get up to 8 seconds of these effects.",
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
	public void onHit(EntityDamageByEntityEvent event) {
		if (checkInstancePlayer(event.getEntity())) {
			if (checkInstancePlayer(event.getDamager())) {
				Player damager = (Player) event.getDamager();
				if (warrior.onCooldown(damager)) {
					CompletableFuture.runAsync(() -> {
						if (hits.get(damager.getUniqueId()) < 8) {
							hits.put(damager.getUniqueId(), hits.get(damager.getUniqueId()) + 1);
							damager.sendMessage(CC.chat("&c&lWarrior Ability &7» &fYou currently have &c" + hits.get(damager.getUniqueId()) + " hits&f."));
						}
					});
				}
			}
		}
	}

	@EventHandler
	public void onInteractWarrior(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getItem() == null)
			return;
		if (event.getAction().name().contains("RIGHT")) {
			if (isSimilar(event.getItem())) {
				if (!canUse(player)) {
					return;
				}
				hits.putIfAbsent(player.getUniqueId(), 0);
				warrior.applyCooldown(player, 10);
				addCooldown(player, 90);
				event.setCancelled(true);
				takeItem(player);

				List<String> hitMsg = Arrays.asList(
						"",
						"&cYou" + " &fhave just used a " + displayName(),
						" ",
						"&7┃ &fYou have 10 seconds to get as many",
						"&7┃ &fhits as you can. After 10 seconds you",
						"&7┃ &fwill receive &cStrength II, Resistance III,",
						"&7┃ &cRegeneration III, and Speed III&f for the amount",
						"&7┃ &fof hits you have in these 10 seconds.",
						"");

				hitMsg.forEach(s -> player.sendMessage(CC.chat(s)));

				List<PotionEffect> effects = new ArrayList<>(player.getActivePotionEffects());

				new BukkitRunnable() {
					@Override
					public void run() {
						PvPClass.setRestoreEffect(player, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * hits.get(player.getUniqueId()), 1));
						PvPClass.setRestoreEffect(player, new PotionEffect(PotionEffectType.REGENERATION, 20 * hits.get(player.getUniqueId()), 2));
						PvPClass.setRestoreEffect(player, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * hits.get(player.getUniqueId()), 2));
						PvPClass.setRestoreEffect(player, new PotionEffect(PotionEffectType.SPEED, 20 * hits.get(player.getUniqueId()), 2));

						new BukkitRunnable() {
							@Override
							public void run() {
								player.addPotionEffects(effects);
							}
						}.runTaskLater(HCF.getInstance(), 20 * hits.get(player.getUniqueId()));

						CompletableFuture.runAsync(() -> hits.remove(player.getUniqueId()));
					}
				}.runTaskLater(HCF.getInstance(), 20 * 10);

			}
		}
	}

}
