package rip.orbit.hcteams.team.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.team.Team;

public class TeamPointBreakDownCommand {

	@Command(names = { "team pointbr", "team pbr", "t pointbr", "t pbr" }, permission = "op")
	public static void teamPointBreakDown(Player player, @Param(name="team", defaultValue="self") Team team) {
		player.sendMessage(ChatColor.GOLD + "Point Breakdown of " + team.getName());

		for (String info : team.getPointBreakDown()) {
			player.sendMessage(info);
		}
	}

}
