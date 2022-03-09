package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

public class TeamUnallyCommand {

    @Command(names={ "team unally", "t unally", "f unally", "faction unally", "fac unally" }, permission="")
    public static void teamUnally(Player sender, @Param(name="team") Team team) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        Team senderTeam = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You need to be in a team to do this.");
            return;
        }

        if (!(senderTeam.isOwner(sender.getUniqueId()) || senderTeam.isCoLeader(sender.getUniqueId()) || senderTeam.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!senderTeam.isAlly(team)) {
            sender.sendMessage(ChatColor.RED + "You are not allied to " + team.getName() + "!");
            return;
        }

        senderTeam.getAllies().remove(team.getUniqueId());
        team.getAllies().remove(senderTeam.getUniqueId());

        senderTeam.flagForSave();
        team.flagForSave();

        for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player.getUniqueId())) {
                player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has dropped their alliance with your team.");
            } else if (senderTeam.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "Your team has dropped its alliance with " + team.getName(sender) + ChatColor.YELLOW + ".");
            }
        }
    }

}