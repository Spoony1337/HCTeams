package rip.orbit.hcteams.blockshop.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.blockshop.menu.BlockShopMenu;
import rip.orbit.hcteams.commands.staff.SOTWCommand;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/08/2021 / 10:54 PM
 * HCTeams / rip.orbit.hcteams.blockshop.command
 */
public class BlockShopCommand {

	@Command(names = "blockshop", permission = "")
	public static void blockshop(Player sender) {
		if (LandBoard.getInstance().getTeam(sender.getLocation()) != null && !SOTWCommand.isSOTWTimer() && !LandBoard.getInstance().getTeam(sender.getLocation()).hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
			sender.sendMessage(CC.translate("&cYou cannot use this here without sotw timer on."));
			return;
		}
		if (LandBoard.getInstance().getTeam(sender.getLocation()) == null && !SOTWCommand.isSOTWTimer()) {
			sender.sendMessage(CC.translate("&cYou cannot use this whilst not in a safezone."));
			return;
		}
		if (LandBoard.getInstance().getTeam(sender.getLocation()) != null && !LandBoard.getInstance().getTeam(sender.getLocation()).hasDTRBitmask(DTRBitmask.SAFE_ZONE) && !SOTWCommand.isSOTWTimer()) {
			sender.sendMessage(CC.translate("&cYou cannot use this whilst not in a safezone."));
			return;
		}
		new BlockShopMenu().openMenu(sender);
	}
}
