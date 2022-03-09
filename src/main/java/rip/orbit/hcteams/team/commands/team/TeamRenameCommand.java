package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

public class TeamRenameCommand {

    @Command(names={ "team rename", "t rename", "f rename", "faction rename", "fac rename" }, permission="")
    public static void teamRename(Player sender, @Param(name="new name") String newName) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You need to be in a team to do this.");
            return;
        }

        if (HCF.getInstance().getCitadelHandler().getCappers().contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Citadel cappers cannot change their name. Please contact an admin to rename your team.");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only team owners and co-leaders can use this command!");
            return;
        }

        if (newName.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
            return;
        }

        if (newName.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
            return;
        }

        if (!TeamCreateCommand.ALPHA_NUMERIC.matcher(newName).find()) {
            if (HCF.getInstance().getTeamHandler().getTeam(newName) == null) {
                team.rename(newName);
                sender.sendMessage(ChatColor.GREEN + "Team renamed to " + newName);
            } else {
                sender.sendMessage(ChatColor.RED + "A team with that name already exists!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
        }
    }

}