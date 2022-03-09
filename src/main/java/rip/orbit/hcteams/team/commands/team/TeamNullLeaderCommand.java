package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

public class TeamNullLeaderCommand {

    @Command(names={ "team nullleader", "t nullleader", "f nullleader", "faction nullleader", "fac nullleader" }, permission="op")
    public static void teamNullLeader(Player sender) {
        int nullLeaders = 0;

        for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() == null) {
                nullLeaders++;
                sender.sendMessage(ChatColor.RED + "- " + team.getName());
            }
        }

        if (nullLeaders == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No null teams found.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + nullLeaders + " null teams found.");
        }
    }

}