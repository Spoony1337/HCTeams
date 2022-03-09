package rip.orbit.hcteams.ability.items;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.profile.Profile;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.JavaUtils;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/07/2021 / 1:35 AM
 * HCTeams / rip.orbit.hcteams.ability.items
 */
public class Curse extends Ability {

	public Cooldowns cd = new Cooldowns();
	public Cooldowns soupCooldown = new Cooldowns();
	public Cooldowns souped = new Cooldowns();
	public ConcurrentHashMap<UUID, Integer> hits = new ConcurrentHashMap<>();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public String name() {
		return "inventorycurse";
	}

	@Override
	public String displayName() {
		return CC.chat("&2&lInventory Curse");
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.ENCHANTED_BOOK;
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
						"&7Hit a player with this to curse their inventory along with their",
						"&7health potions to switch them to soup. If they use a soup they are",
						"&7placed on cooldown for 5 seconds and will not be able to use a soup",
						"&7within that time.",
						" ",
						"&c&lNOTE&7: They will still be able to use the soups as if it was soup pvp.",
						" "
				)
		);
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

			List<String> beenHitMsg = Arrays.asList(
					"",
					"&2&lYOU HAVE BEEN HIT!",
					" ",
					"&2" + damager.getName() + " &fhas just hit you with",
					"&fan &2Inventory Curse&f.",
					" ",
					"&7┃ &fYour pots are now clickable soups",
					"&7┃ &ffor &215 seconds&f.",
					"");

			List<String> hitMsg = Arrays.asList(
					"",
					"&2&lYOU HAVE HIT SOMEONE!",
					" ",
					"&2You" + " &fhave just hit &2" + damaged.getName(),
					"&fwith an &2Inventory Curse&f.",
					" ",
					"&7┃ &fTheir pots are now clickable soups",
					"&7┃ &ffor &215 seconds&f.",
					"");

			hitMsg.forEach(s -> damager.sendMessage(CC.chat(s)));

			beenHitMsg.forEach(s -> damaged.sendMessage(CC.chat(s)));

			HCF.getInstance().getAbilityHandler().getAbilityEffect().applyCooldown(damaged, 15);
			souped.applyCooldown(damaged, 15);
			addCooldown(damager, 90);
			takeItem(damager);

			convertInv(damaged);

			CompletableFuture.runAsync(() -> hits.remove(damager.getUniqueId()));
			new BukkitRunnable() {
				@Override
				public void run() {
					returnInv(damaged);
				}
			}.runTaskLater(HCF.getInstance(), 20 * 15);
			return;
		}

		if (profile.canHit(profile.getCurseHitTime())) {
			CompletableFuture.runAsync(() -> hits.remove(damager.getUniqueId()));
		}

		addHits(damager, hits);

		profile.setCurseHitTime(System.currentTimeMillis() + JavaUtils.parse("15s"));
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (souped.onCooldown(event.getEntity())) {
			returnInv(event.getEntity());
			event.getDrops().clear();
			for (ItemStack content : event.getEntity().getInventory().getContents()) {
				if (content != null && content.getType() != Material.AIR) {
					event.getDrops().add(content);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (souped.onCooldown(event.getPlayer())) {
			if (!event.hasItem() || event.getItem().getType() != Material.MUSHROOM_SOUP || !event.getAction().name().contains("RIGHT_")) {
				return;
			}

			Player player = event.getPlayer();

			if (player.getHealth() <= 19 && !soupCooldown.onCooldown(player)) {
				double current = player.getHealth();
				double max = player.getMaxHealth();

				player.getItemInHand().setType(Material.BOWL);
				player.setHealth(Math.min(max, current + 7D));
				soupCooldown.applyCooldown(player, 4);
			} else {
				if (soupCooldown.onCooldown(player)) {
					player.sendMessage(CC.chat("&cYou cannot soup for " + soupCooldown.getRemaining(player)));
				} else {
					player.sendMessage(CC.chat("&cYou have to lose health before you do that!"));
				}
			}
		}
	}

	public void convertInv(Player damaged) {
		ItemStack[] contentsArray = damaged.getInventory().getContents();
		List<ItemStack> contentsList = new ArrayList<>();
		List<ItemStack> replacedContents = new ArrayList<>();

		contentsList.addAll(Arrays.asList(contentsArray));

		for (ItemStack stack : contentsList) {
			if (stack != null && !stack.getType().equals(Material.AIR)) {
				if (stack.getType() == Material.POTION) {
					if (stack.getDurability() == 16421) {
						stack.setType(Material.MUSHROOM_SOUP);
						stack.setDurability((short) 0);
					}
				}
				replacedContents.add(stack);
			}
		}

		int i = 0;
		for (ItemStack replacedContent : replacedContents) {
			if (replacedContent == null || replacedContent.getType() == Material.AIR) {
				++i;
				continue;
			}
			damaged.getInventory().setItem(i, replacedContent);
			++i;
		}

		damaged.updateInventory();
	}

	public void returnInv(Player damaged) {
		ItemStack[] contentsArray = damaged.getInventory().getContents();
		List<ItemStack> contentsList = new ArrayList<>();
		List<ItemStack> replacedContents = new ArrayList<>();

		contentsList.addAll(Arrays.asList(contentsArray));

		for (ItemStack stack : contentsList) {
			if (stack != null && !stack.getType().equals(Material.AIR)) {

				if (stack.getType() == Material.BOWL) {
					stack.setType(Material.AIR);
				}
				if (stack.getType() == Material.MUSHROOM_SOUP) {
					stack.setType(Material.POTION);
					stack.setDurability((short) 16421);
				}
				replacedContents.add(stack);

			}
		}

		int i = 0;
		for (ItemStack replacedContent : replacedContents) {
			if (replacedContent == null || replacedContent.getType() == Material.AIR) {
				++i;
				continue;
			}
			damaged.getInventory().setItem(i, replacedContent);
			++i;
		}

		damaged.updateInventory();
	}


}
