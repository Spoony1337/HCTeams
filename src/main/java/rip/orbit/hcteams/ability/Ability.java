package rip.orbit.hcteams.ability;

import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.items.Dome;
import rip.orbit.hcteams.commands.staff.SOTWCommand;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;
import rip.orbit.hcteams.util.object.ItemBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 30/06/2021 / 12:51 PM
 * HCTeams / rip.orbit.hcteams.ability
 */
public abstract class Ability implements Listener {

	public Ability() {
		Bukkit.getPluginManager().registerEvents(this, HCF.getInstance());
	}

	public abstract Cooldowns cooldown();
	public abstract String name();
	public abstract String displayName();
	public abstract int data();
	public abstract Material mat();
	public abstract boolean glow();
	public abstract List<String> lore();
	public abstract List<String> foundInfo();

	@Setter private ItemStack stack = new ItemBuilder(mat()).lore(lore()).data(data()).name(CC.chat(displayName())).build();

	public ItemStack getStack() {
		if (glow()) {
			stack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		}
		return stack;
	}

	public boolean canAttack(Player attacker, Player damaged) {
		Team attackerTeam = HCF.getInstance().getTeamHandler().getTeam(attacker);
		Team damagedTeam = HCF.getInstance().getTeamHandler().getTeam(damaged);

		if (attacker.getLocation().distance(attacker.getWorld().getSpawnLocation()) < 850) {
			attacker.sendMessage(CC.chat("&cYou cannot use ability items before 850 blocks."));
			return false;
		}

		if (Dome.antiAbility.onCooldown(attacker)) {
			attacker.sendMessage(CC.translate("&cYou cannot do this for &l" + Dome.antiAbility.getRemaining(attacker)));
			return false;
		}
		if (DTRBitmask.CONQUEST.appliesAt(damaged.getLocation()) || DTRBitmask.CONQUEST.appliesAt(attacker.getLocation())) {
			attacker.sendMessage(CC.chat("&cYou cannot use ability items inside of conquest's territory."));
			return false;
		}
		if (DTRBitmask.KOTH.appliesAt(damaged.getLocation()) || DTRBitmask.KOTH.appliesAt(attacker.getLocation())) {
			attacker.sendMessage(CC.chat("&cYou cannot use ability items inside of koth's territory."));
			return false;
		}
		if (DTRBitmask.CITADEL.appliesAt(damaged.getLocation()) || DTRBitmask.CITADEL.appliesAt(attacker.getLocation())) {
			attacker.sendMessage(CC.chat("&cYou cannot use ability items inside of citadel's territory."));
			return false;
		}

		if (DTRBitmask.SAFE_ZONE.appliesAt(damaged.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(attacker.getLocation())) {
			attacker.sendMessage(CC.chat("&cYou cannot use ability items inside of spawn's territory."));
			return false;
		}
		if (attacker.getWorld().getEnvironment() == World.Environment.THE_END) {
			attacker.sendMessage(CC.chat("&cYou cannot use ability items inside of end."));
			return false;
		}
		if (attacker.getWorld().getEnvironment() == World.Environment.NETHER) {
			attacker.sendMessage(CC.chat("&cYou cannot use ability items inside of nether."));
			return false;
		}

		if (HCF.getInstance().getPvPTimerMap().hasTimer(attacker.getUniqueId())) {
			return false;
		}


		if (HCF.getInstance().getPvPTimerMap().hasTimer(damaged.getUniqueId())) {
			return false;
		}

		if (attackerTeam == null) {
			return true;
		}
		if (damagedTeam == null) {
			return true;
		}

		return !damagedTeam.getOnlineMembers().contains(attacker);
	}

	public boolean isClick(PlayerInteractEvent event, String click) {
		return event.getAction().name().contains(click);
	}

	public boolean canUse(Player player) {

		if (HCF.getInstance().getAbilityHandler().getAbilityCD().onCooldown(player)) {
			player.sendMessage(CC.chat("&cYou are currently on &eAbility Item" + "&c cooldown for &l" + HCF.getInstance().getAbilityHandler().getAbilityCD().getRemaining(player)));
			return false;
		}
		if (Dome.antiAbility.onCooldown(player)) {
			player.sendMessage(CC.translate("&cYou cannot do this for &l" + Dome.antiAbility.getRemaining(player)));
			return false;
		}
		if (player.getLocation().distance(player.getWorld().getSpawnLocation()) < 850) {
			player.sendMessage(CC.chat("&cYou cannot use ability items before 850 blocks."));
			return false;
		}

		if (cooldown().onCooldown(player)) {
			player.sendMessage(CC.chat("&cYou are currently on " + displayName() + "&c cooldown for &l" + cooldown().getRemaining(player)));
			return false;
		}
		if (DTRBitmask.CITADEL.appliesAt(player.getLocation())) {
			player.sendMessage(CC.chat("&cYou cannot use ability items inside of citadel's territory."));
			return false;
		}
		if (DTRBitmask.CONQUEST.appliesAt(player.getLocation())) {
			player.sendMessage(CC.chat("&cYou cannot use ability items inside of conquest's territory."));
			return false;
		}

		if (DTRBitmask.KOTH.appliesAt(player.getLocation())) {
			player.sendMessage(CC.chat("&cYou cannot use ability items inside of koth's territory."));
			return false;
		}

		if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
			player.sendMessage(CC.chat("&cYou cannot use ability items inside of spawn's territory."));
			return false;
		}
		if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
			player.sendMessage(CC.chat("&cYou cannot use ability items whilst your pvp timer is active."));
			return false;
		}
		if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(player.getUniqueId())) {
			player.sendMessage(CC.chat("&cYou have to be sotw enabled to do this."));
			return false;
		}

		return true;
	}

	public boolean isSimilar(PlayerInteractEvent event) {
		if (event.getItem() == null) return false;

		return event.getItem().isSimilar(getStack());

	}

	public boolean isSimilar(ItemStack item) {
		if (item == null) return false;

		return item.isSimilar(getStack());

	}

	public boolean isSimilar(PlayerInteractEvent event, ItemStack stack) {
		if (event.getItem() == null) return false;

		return event.getItem().isSimilar(stack);

	}

	public void addCooldown(Player player, int seconds) {
		cooldown().applyCooldown(player, seconds);
		HCF.getInstance().getAbilityHandler().getAbilityCD().applyCooldown(player, 10);
	}

	public boolean checkInstanceSnowball(Object instance) {
		return instance instanceof Snowball;
	}

	public boolean checkInstancePlayer(Object instance) {
		return instance instanceof Player;
	}

	public boolean checkInstanceEgg(Object instance) {
		return instance instanceof Egg;
	}

	public boolean isInDistance(Player player, Player target, int distance) {
		return player.getLocation().distance(target.getLocation()) < distance;
	}

	public boolean isInDistance(Location player, Location target, int distance) {
		return player.distance(target) < distance;
	}

	public void takeItem(Player player) {
		if (player.getItemInHand().getAmount() > 1) {
			player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
		} else {
			player.setItemInHand(null);
		}
	}

	public void addHits(Player player, Map<UUID, Integer> hits) {

		CompletableFuture.runAsync(() -> {
			hits.putIfAbsent(player.getUniqueId(), 1);
			hits.put(player.getUniqueId(), hits.get(player.getUniqueId()) + 1);
		});
	}

	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		if (event.getItem() == null)
			return;

		if (isSimilar(event.getItem())) {
			if (cooldown().onCooldown(event.getPlayer())) {
				event.getPlayer().sendMessage(CC.chat("&cYou are currently on " + displayName() + "&c cooldown for &l" + cooldown().getRemaining(event.getPlayer())));

			}
		}
	}
}
