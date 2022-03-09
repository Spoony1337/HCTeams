package rip.orbit.hcteams.team.commands;


import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

public class FocusCommand {

    @Command(names={ "focus" }, permission="")
    public static void focus(Player sender, @Param(name="player") Player target) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        Team senderTeam = HCF.getInstance().getTeamHandler().getTeam(sender);
        Team targetTeam = HCF.getInstance().getTeamHandler().getTeam(target);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You need to be in a team to do this.");
            return;
        }


        if (senderTeam == targetTeam) {
            sender.sendMessage(ChatColor.RED + "You cannot target a player on your team.");
            return;
        }

        senderTeam.setFocused(target.getUniqueId());
        senderTeam.setFocused(target.getUniqueId());
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has been focused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

    }

}