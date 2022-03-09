package rip.orbit.hcteams.events.citadel.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.citadel.CitadelHandler;
import rip.orbit.hcteams.team.Team;

import java.util.ArrayList;
import java.util.List;

public class CitadelSetCapperCommand {

    @Command(names={ "citadel setcapper" }, permission="op")
    public static void citadelSetCapper(Player sender, @Param(name="cappers") String cappers) {
        if (cappers.equals("null")) {
            HCF.getInstance().getCitadelHandler().resetCappers();
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Reset Citadel cappers.");
        } else {
            String[] teamNames = cappers.split(",");
            List<ObjectId> teams = new ArrayList<>();

            for (String teamName : teamNames) {
                Team team = HCF.getInstance().getTeamHandler().getTeam(teamName);

                if (team != null) {
                    teams.add(team.getUniqueId());
                } else {
                    sender.sendMessage(ChatColor.RED + "Team '" + teamName + "' cannot be found.");
                    return;
                }
            }

            HCF.getInstance().getCitadelHandler().getCappers().clear();
            HCF.getInstance().getCitadelHandler().getCappers().addAll(teams);
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Updated Citadel cappers.");
        }
    }

}