package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

import java.util.UUID;

public class TeamInvitesCommand {

    @Command(names={ "team invites", "t invites", "f invites", "faction invites", "fac invites" }, permission="")
    public static void teamInvites(Player sender) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        StringBuilder yourInvites = new StringBuilder();

        for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
            if (team.getInvitations().contains(sender.getUniqueId())) {
                yourInvites.append(ChatColor.GRAY).append(team.getName()).append(ChatColor.YELLOW).append(", ");
            }
        }

        if (yourInvites.length() > 2) {
            yourInvites.setLength(yourInvites.length() - 2);
        } else {
            yourInvites.append(ChatColor.GRAY).append("No pending invites.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Your Invites: " + yourInvites.toString());

        Team current = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (current != null) {
            StringBuilder invitedToYourTeam = new StringBuilder();

            for (UUID invited : current.getInvitations()) {
                invitedToYourTeam.append(ChatColor.GRAY).append(UUIDUtils.name(invited)).append(ChatColor.YELLOW).append(", ");
            }

            if (invitedToYourTeam.length() > 2) {
                invitedToYourTeam.setLength(invitedToYourTeam.length() - 2);
            } else {
                invitedToYourTeam.append(ChatColor.GRAY).append("No pending invites.");
            }

            sender.sendMessage(ChatColor.YELLOW + "Invited to your Team: " + invitedToYourTeam.toString());
        }
    }

}