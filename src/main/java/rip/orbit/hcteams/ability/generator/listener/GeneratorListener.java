package rip.orbit.hcteams.ability.generator.listener;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.generator.Generator;
import rip.orbit.hcteams.ability.generator.GeneratorHandler;
import rip.orbit.hcteams.ability.generator.menu.GeneratorMenu;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 4:33 AM
 * HCTeams / rip.orbit.hcteams.ability.generator.listener
 */
public class GeneratorListener implements Listener {

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getBlock().getWorld().getEnvironment() == World.Environment.THE_END)
			return;
		if (event.getBlock().getWorld().getEnvironment() == World.Environment.NETHER)
			return;
		GeneratorHandler handler = HCF.getInstance().getAbilityHandler().getGeneratorHandler();
		Player p = event.getPlayer();

		Generator generator = handler.byLocation(event.getBlock().getLocation());

		if (generator != null) {
			p.sendMessage(CC.translate("&cYou cannot break generators."));
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		GeneratorHandler handler = HCF.getInstance().getAbilityHandler().getGeneratorHandler();
		Player p = event.getPlayer();

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (event.getClickedBlock() == null)
			return;

		if (event.getClickedBlock().getWorld().getEnvironment() == World.Environment.THE_END)
			return;
		if (event.getClickedBlock().getWorld().getEnvironment() == World.Environment.NETHER)
			return;

		Generator generator = handler.byLocation(event.getClickedBlock().getLocation());

		if (generator != null) {
			if (generator.displayMaterial() != event.getClickedBlock().getType())
				return;

			if (DTRBitmask.ROAD.appliesAt(event.getClickedBlock().getLocation()))
				return;
			if (DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation()))
				return;

			new GeneratorMenu(generator).openMenu(p);
		}
	}

}
