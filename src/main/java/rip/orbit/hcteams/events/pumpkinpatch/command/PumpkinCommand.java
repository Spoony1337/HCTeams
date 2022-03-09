package rip.orbit.hcteams.events.pumpkinpatch.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.pumpkinpatch.listener.PumpkinListener;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.menu.Button;
import rip.orbit.hcteams.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/09/2021 / 8:56 PM
 * HCTeams / rip.orbit.hcteams.events.pumpkinpatch.command
 */
public class PumpkinCommand {

	@Command(names = {"pumpkin", "patch", "pumpkinpatch"}, permission = "")
	public static void patch(CommandSender sender) {
		Team team = HCF.getInstance().getTeamHandler().getTeam("PumpkinPatch");
		sender.sendMessage(CC.translate(" "));
		sender.sendMessage(CC.translate("&6&lPumpkin Patch Info"));
		sender.sendMessage(CC.translate(" "));
		String loc = "" + team.getHq().getBlockX() + ", " + team.getHq().getBlockZ();
		sender.sendMessage(CC.translate("&fPumpkin Patch is location @ &6" + loc));
		sender.sendMessage(CC.translate("&fWorld&7: &6Overworld"));
		sender.sendMessage(CC.translate("&fMined Pumpkins&7: &6" + PumpkinListener.mined + "/" + HCF.getInstance().getPumpkinPatchHandler().getLocations().size()));
		sender.sendMessage(CC.translate(" "));

	}

	@Command(names = {"pumpkin respawn", "patch respawn", "pumpkinpatch respawn"}, permission = "op")
	public static void respawn(CommandSender sender) {

		HCF.getInstance().getPumpkinPatchHandler().respawn();

		sender.sendMessage(CC.translate("&aRespawned"));
	}

	@Command(names = {"pumpkin loot", "patch loot", "pumpkinpatch loot"}, permission = "")
	public static void patchLoot(Player sender) {
		new Menu() {

			@Override
			public String getTitle(Player player) {
				return CC.translate("&6Pumpkin Patch Loot");
			}

			@Override
			public int size() {
				return 27;
			}

			@Override
			public Map<Integer, Button> getButtons(Player player) {
				Map<Integer, Button> buttons = new HashMap<>();



				return buttons;
			}
		};
	}

	@Command(names = "pumpkin scan", permission = "op")
	public static void scan(CommandSender sender) {
		HCF.getInstance().getPumpkinPatchHandler().scanClaim();
		sender.sendMessage(CC.translate("&aScanned claim for pumpkins."));
	}

}
