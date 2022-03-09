package rip.orbit.hcteams.team.commands.team;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;

import java.awt.*;

public class TeamFocusCommand {


	@Command(names = {"f focus", "t focus", "faction focus", "team focus"}, permission = "")
	public static void focus(Player sender, @Param(name = "player") Team targetTeam) {

		Team senderTeam = HCF.getInstance().getTeamHandler().getTeam(sender);

		if (senderTeam == null) {
			sender.sendMessage(ChatColor.GRAY + "You need to be in a team to do this.");
			return;
		}

		if (senderTeam == targetTeam) {
			sender.sendMessage(ChatColor.RED + "You cannot focus a player on your team!");
			return;
		}

		if (senderTeam.getFactionFocused() != null) {
			if (senderTeam.getFactionFocused().getHq() != null) {
				LCWaypoint waypoint = new LCWaypoint(senderTeam.getFactionFocused().getName() + "'s HQ", senderTeam.getFactionFocused().getHq(), Color.orange.hashCode(), true);
				senderTeam.getOnlineMembers().forEach(m -> {
					LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
				});
			}
		}

		if (targetTeam.getHq() != null) {
			LCWaypoint waypoint = new LCWaypoint(targetTeam.getName() + "'s HQ", targetTeam.getHq(), Color.orange.hashCode(), true);
			senderTeam.getOnlineMembers().forEach(m -> {
				LunarClientAPI.getInstance().sendWaypoint(m, waypoint);
			});
		}
		senderTeam.setFactionFocus(targetTeam);
		senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + targetTeam.getName() + ChatColor.YELLOW + " has been focused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

	}

	@Command(names = {"f unfocus", "t unfocus", "faction unfocus", "team unfocus"}, permission = "")
	public static void unfocus(Player sender) {

		Team senderTeam = HCF.getInstance().getTeamHandler().getTeam(sender);

		if (senderTeam == null) {
			sender.sendMessage(ChatColor.RED + "You need to be in a team to do this.");
			return;
		}

		if (senderTeam.getFactionFocused() == null) {
			sender.sendMessage(ChatColor.RED + "Your team doesn't have a team focused!");
			return;
		}

		if (senderTeam.getFactionFocused().getHq() != null) {
			LCWaypoint waypoint = new LCWaypoint(senderTeam.getFactionFocused().getName() + "'s HQ", senderTeam.getFactionFocused().getHq(), Color.orange.hashCode(), true);
			senderTeam.getOnlineMembers().forEach(m -> {
				LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
			});
		}

		senderTeam.sendMessage(CC.chat("&d" + sender.getName() + " &ehas just cleared the team focus."));
		senderTeam.setFactionFocus(null);
	}
}