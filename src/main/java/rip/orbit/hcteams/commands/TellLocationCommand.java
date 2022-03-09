package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.HCFConstants;
import rip.orbit.hcteams.team.Team;

public class TellLocationCommand {

    @Command(names = {"telllocation", "tl"}, permission = "")
    public static void tellLocation(Player sender) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You're not on a team!");
            return;
        }

        Location l = sender.getLocation();
        team.sendMessage(HCFConstants.teamChatFormat(sender, String.format("[%.1f, %.1f, %.1f]", l.getX(), l.getY(), l.getZ())));
    }

}
