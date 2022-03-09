package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.CC;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 12/07/2021 / 7:33 AM
 * HCTeams / rip.orbit.hcteams.commands.staff
 */
public class ToggleLunarNametagsCommand {

	@Command(names = {"togglenametagsabove", "togglenametagsabovehead", "togglenametagabove"}, permission = "foxtrot.togglenamtags", description = "command used to toggle lunar nametags incase of lag issues")
	public static void togglelunarnametags(CommandSender sender, @Param(name = "yes|no") String yesOrno) {
		if (HCF.getInstance().getScoreboardManager().isLunarEnabled()) {
			HCF.getInstance().getScoreboardManager().setLunarEnabled(false);
			sender.sendMessage(CC.chat("&fLunar nametags have been toggled off"));
			if (yesOrno.equalsIgnoreCase("yes")) {
				Bukkit.broadcastMessage(CC.chat("&aNametags above head are now toggled off for everyone on LunarClient or CheatBreaker temporarily. They will be enabled soon. Please keep patient."));
			}
		} else {
			HCF.getInstance().getScoreboardManager().setLunarEnabled(true);
			sender.sendMessage(CC.chat("&fLunar nametags have been toggled back on"));
			if (yesOrno.equalsIgnoreCase("yes")) {
				Bukkit.broadcastMessage(CC.chat("&aNameTags above head are now toggled back on for everyone on LunarClient or CheatBreaker!"));
			}
		}
	}

}
