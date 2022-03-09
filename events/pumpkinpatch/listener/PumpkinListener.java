package rip.orbit.hcteams.events.pumpkinpatch.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/09/2021 / 9:04 PM
 * HCTeams / rip.orbit.hcteams.events.pumpkinpatch.listener
 */
public class PumpkinListener implements Listener {

	public static int mined = 0;

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent event) {
		Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

		if (team != null) {
			if (team.getName().equals("PumpkinPatch")) {
				event.getBlock().getDrops().clear();
				for (ItemStack stack : HCF.getInstance().getPumpkinPatchHandler().getLoot()) {
					event.getBlock().getDrops().add(stack);
				}
				mined++;
			}
		}

	}

}
