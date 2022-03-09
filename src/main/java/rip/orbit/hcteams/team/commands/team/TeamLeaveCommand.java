package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;

public class TeamLeaveCommand {

    @Command(names={ "team leave", "t leave", "f leave", "faction leave", "fac leave" }, permission="")
    public static void teamLeave(Player sender) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You need to be in a team to do this.");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
            return;
        }

        if(SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(ChatColor.RED + "You are combat-tagged! You can only leave your faction by using '" + ChatColor.YELLOW + "/f forceleave" + ChatColor.RED + "' which will cost your team 1 DTR.");
            return;
        }

        if (team.removeMember(sender.getUniqueId())) {
            team.disband();
            HCF.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
        } else {
            HCF.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            team.flagForSave();
            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");

            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
        }

//        FrozenNametagHandler.reloadPlayer(sender);
//        FrozenNametagHandler.reloadOthersFor(sender);
    }

}