package rip.orbit.hcteams.stars.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.commands.staff.SOTWCommand;
import rip.orbit.hcteams.persist.maps.StarsMap;
import rip.orbit.hcteams.stars.menu.StarShopMenu;
import rip.orbit.hcteams.team.claims.LandBoard;
import rip.orbit.hcteams.team.dtr.DTRBitmask;
import rip.orbit.hcteams.util.CC;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 6:04 AM
 * HCTeams / rip.orbit.hcteams.stars.command
 */
public class StarsCommand {

	@Command(names = "stars", permission = "")
	public static void stars(Player sender) {
		StarsMap map = HCF.getInstance().getStarsMaps();
		int amount = map.get(sender.getUniqueId());
		sender.sendMessage(CC.translate("&fStars&7: &6" + amount + "✧"));
	}

	@Command(names = "starshop", permission = "")
	public static void starshop(Player sender) {
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
		new StarShopMenu().openMenu(sender);
	}

	@Command(names = {"stars set"}, permission = "op")
	public static void setStars(CommandSender sender, @Param(name = "target") UUID target, @Param(name = "amount") int amount) {
		StarsMap map = HCF.getInstance().getStarsMaps();
		map.set(target, amount);
		sender.sendMessage(CC.translate("&aSuccess, the players stars is now " + amount));
	}

	@Command(names = {"stars add"}, permission = "op")
	public static void addStars(CommandSender sender, @Param(name = "target") UUID target, @Param(name = "amount") int amount) {
		StarsMap map = HCF.getInstance().getStarsMaps();
		int toAdd = map.get(target) + amount;
		map.set(target, toAdd);
		sender.sendMessage(CC.translate("&aSuccess, the players stars is now " + toAdd));
	}

}
