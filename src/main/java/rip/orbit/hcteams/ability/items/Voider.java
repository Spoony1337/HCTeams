package rip.orbit.hcteams.ability.items;

import net.minecraft.server.v1_7_R4.EntityEnderSignal;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.Ability;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.cooldown.Cooldowns;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/08/2021 / 9:47 PM
 * HCTeams / rip.orbit.hcteams.ability.items
 */
public class Voider extends Ability {

	public Cooldowns cd = new Cooldowns();

	@Override
	public Cooldowns cooldown() {
		return cd;
	}

	@Override
	public List<String> lore() {
		return CC.translate(Arrays.asList(
				" ",
				"&7Right Click any block to set it to",
				"&7air for 15 seconds.",
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
		return CC.chat("&d&lVoider");
	}

	@Override
	public String name() {
		return "voider";
	}

	@Override
	public int data() {
		return 0;
	}

	@Override
	public Material mat() {
		return Material.EYE_OF_ENDER;
	}

	@Override
	public boolean glow() {
		return true;
	}

	@EventHandler
	public void onProjectLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof EntityEnderSignal) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getItem() == null)
			return;
		if (event.getClickedBlock() == null)
			return;
		if (isSimilar(event.getItem())) {
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
				event.setCancelled(true);
				event.setUseItemInHand(Event.Result.DENY);
				return;
			}
			if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (!canUse(player)) {
					return;
				}
				addCooldown(player, 90);
				event.setCancelled(true);
				takeItem(player);

				List<String> hitMsg = Arrays.asList(
						"",
						"&dYou &fhave just activated a " + displayName() + "&f.",
						" ",
						"&7┃ &fYou have just removed a block.",
						"");

				hitMsg.forEach(s -> player.sendMessage(CC.chat(s)));

				activate(event.getClickedBlock(), event.getClickedBlock().getType());
			}
		}
	}

	private void activate(Block block, Material previous) {
		block.setType(Material.AIR);
		new BukkitRunnable() {
			@Override
			public void run() {
				block.setType(previous);
			}
		}.runTaskLater(HCF.getInstance(), 20 * 15);
	}

}
