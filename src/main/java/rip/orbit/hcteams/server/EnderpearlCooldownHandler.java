package rip.orbit.hcteams.server;

import lombok.Getter;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.server.event.EnderpearlCooldownAppliedEvent;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.dtr.DTRBitmask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnderpearlCooldownHandler implements Listener {

	@Getter private static Map<String, Long> enderpearlCooldown = new ConcurrentHashMap<>();

	@Command(names = "timer set enderpearl", permission = "reset.pearl")
	public static void reset(CommandSender sender, @Param(name = "player") Player uuid, @Param(name = "0") String string) {

		enderpearlCooldown.remove(uuid.getName());
	}


	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) event.getEntity().getShooter();

		if (event.getEntity() instanceof EnderPearl) {
//			// Cancel if the player used a Fake Pearl Ability Item
//			if(shooter.hasMetadata("FakePearl")) return;

			// Store the player's enderpearl in-case we need to remove it prematurely
			shooter.setMetadata("LastEnderPearl", new FixedMetadataValue(HCF.getInstance(), event.getEntity()));

			// Get the default time to apply (in MS)
			long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(event.getEntity().getLocation()) ? 30_000L : HCF.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L;

			// Call our custom event (time to apply needs to be modifiable)
			EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(shooter, timeToApply);
			HCF.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

			// Put the player into the cooldown map
			enderpearlCooldown.put(shooter.getName(), System.currentTimeMillis() + appliedEvent.getTimeToApply());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof EnderPearl)) {
			return;
		}

		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player thrower = (Player) event.getEntity().getShooter();

		if (enderpearlCooldown.containsKey(thrower.getName()) && enderpearlCooldown.get(thrower.getName()) > System.currentTimeMillis()) {
//			// Cancel if the player used a Fake Pearl Ability Item
//			if(thrower.hasMetadata("FakePearl")) return;

			long millisLeft = enderpearlCooldown.get(thrower.getName()) - System.currentTimeMillis();

			double value = (millisLeft / 1000D);
			double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1; // don't tell user 0.0

			event.setCancelled(true);
			thrower.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
			thrower.updateInventory();
		}
	}

	@EventHandler
	public void check2(PlayerInteractEvent event){
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

			if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
				if (enderpearlCooldown.containsKey(player.getName()) && enderpearlCooldown.get(player.getName()) > System.currentTimeMillis()) {
					long millisLeft = enderpearlCooldown.get(player.getName()) - System.currentTimeMillis();

					double value = (millisLeft / 1000D);
					double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1; // don't tell user 0.0

					event.setCancelled(true);
					//player.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
					player.updateInventory();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			return;
		} else if (!enderpearlCooldown.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true); // only reason for this would be player died before pearl landed, so cancel it!
			return;
		}
//		// Cancel if the player used a Fake Pearl Ability Item
//		if(event.getPlayer().hasMetadata("FakePearl")) return;

		Location target = event.getTo();
		Location from = event.getFrom();

		if (DTRBitmask.SAFE_ZONE.appliesAt(target)) {
			if (!DTRBitmask.SAFE_ZONE.appliesAt(from)) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into spawn!");
				enderpearlCooldown.remove(event.getPlayer().getName());

			    event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
				event.getPlayer().updateInventory();
				return;
			}
		}

		if (DTRBitmask.NO_ENDERPEARL.appliesAt(target)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into this region!");
			return;
		}

		Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

		if (HCF.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId()) && ownerTo != null) {
			if (ownerTo.isMember(event.getPlayer().getUniqueId())) {
				HCF.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());
			} else if (ownerTo.getOwner() != null || (DTRBitmask.KOTH.appliesAt(event.getTo()) || DTRBitmask.CITADEL.appliesAt(event.getTo()))) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into claims while having a PvP Timer!");
				return;
			}
		}
	}

//	@EventHandler
//	public void onRefund(PlayerPearlRefundEvent event) {
//		Player player = event.getPlayer();
//
//		if (!player.isOnline()) {
//			return;
//		}
//
//		ItemStack inPlayerHand = player.getItemInHand();
//		if (inPlayerHand != null && inPlayerHand.getType() == Material.ENDER_PEARL && inPlayerHand.getAmount() < 16) {
//			inPlayerHand.setAmount(inPlayerHand.getAmount() + 1);
//			player.updateInventory();
//		}
//
//		enderpearlCooldown.remove(player.getName());
//	}

	public boolean clippingThrough(Location target, Location from, double thickness) {
		return ((from.getX() > target.getX() && (from.getX() - target.getX() < thickness)) || (target.getX() > from.getX() && (target.getX() - from.getX() < thickness)) ||
		        (from.getZ() > target.getZ() && (from.getZ() - target.getZ() < thickness)) || (target.getZ() > from.getZ() && (target.getZ() - from.getZ() < thickness)));
	}

	public static void resetEnderpearlTimer(Player player) {
		if (DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(player.getLocation())) {
			enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + 30_000L);
		} else {
			enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + (HCF.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L));
		}
	}

}