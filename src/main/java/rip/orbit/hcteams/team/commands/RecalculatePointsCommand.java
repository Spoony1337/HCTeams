package rip.orbit.hcteams.team.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

public class RecalculatePointsCommand {
    
    @Command(names = {"team recalculatepoints", "f recalculatepoints", "team recalcpoints"}, permission = "op")
    public static void recalculate(CommandSender sender) {
        int changed = 0;
        
        for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
            int oldPoints = team.getPoints();
            team.recalculatePoints();
            if (team.getPoints() != oldPoints) {
                team.flagForSave();
                sender.sendMessage(ChatColor.YELLOW + "Changed " + team.getName() + "'s points from " + oldPoints + " to " + team.getPoints());
                changed++;
            }

        }
        
        sender.sendMessage(ChatColor.YELLOW + "Changed a total of " + changed + " teams points.");
    }
    
}
