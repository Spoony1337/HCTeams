package rip.orbit.hcteams.team.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

public class ForceLeaveCommand {

    @Command(names={ "forceleave" }, permission="foxtrot.forceleave")
    public static void forceLeave(Player player) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You need to be in a team to do this.");
            return;
        }

        team.removeMember(player.getUniqueId());
        HCF.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);
        player.sendMessage(ChatColor.YELLOW + "Force left your team.");
    }

}