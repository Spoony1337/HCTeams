package rip.orbit.hcteams.team.commands.team;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

import java.awt.*;

public class TeamRallyCommand {

	@Command(names = {"team rally", "t rally", "f rally", "faction rally", "fac rally", "team setrally", "t setrally", "f setrally", "faction setrally", "fac setrally"}, permission = "")
	public static void rally(Player sender) {
		Team senderTeam = HCF.getInstance().getTeamHandler().getTeam(sender);
		if (senderTeam == null) {
			sender.sendMessage(ChatColor.GRAY + "You must be on a team to run this command.");
			return;
		}
		if (senderTeam.getRallyTask() != null) {
			senderTeam.getRallyTask().cancel();
			senderTeam.setRallyTask(null);
		}

		if (senderTeam.getRally() != null) {
			LCWaypoint waypoint = new LCWaypoint("Rally", senderTeam.getRally(), Color.yellow.hashCode(), true);
			senderTeam.getOnlineMembers().forEach(m -> {
				LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
			});
		}

		senderTeam.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's rally point! This will last for 5 minutes.");
		LCWaypoint waypoint = new LCWaypoint("Rally", sender.getLocation(), Color.yellow.hashCode(), true);
		senderTeam.getOnlineMembers().forEach(m -> {
			LunarClientAPI.getInstance().sendWaypoint(m, waypoint);
		});
		senderTeam.setRally(sender.getLocation());

		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				LCWaypoint waypoint = new LCWaypoint("Rally", sender.getLocation(), Color.yellow.hashCode(), true);
				senderTeam.getOnlineMembers().forEach(m -> {
					LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
					senderTeam.setRally(null);
				});
			}
		}.runTaskLater(HCF.getInstance(), 20 * 60 * 5);
		senderTeam.setRallyTask(task);
	}
}
