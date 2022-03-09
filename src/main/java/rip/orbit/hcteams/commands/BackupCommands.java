package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/09/2021 / 3:44 PM
 * HCTeams / rip.orbit.hcteams.commands
 */
public class BackupCommands {

	@Command(names = {"backupteams", "backupcurrentteams"}, permission = "op", async = true)
	public static void backUpTeams(CommandSender sender) {
		HCF.getInstance().backupTeams();
	}

}
