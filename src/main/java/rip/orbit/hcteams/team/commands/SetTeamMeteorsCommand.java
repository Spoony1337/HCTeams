package rip.orbit.hcteams.team.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.team.Team;

public class SetTeamMeteorsCommand {

    @Command(names={ "setteammeteors", "setteammeteors" }, permission="foxtrot.setteambalance")
    public static void setTeamBalance(Player sender, @Param(name="team") Team team, @Param(name="meteors") int balance) {
        team.setMeteors(balance);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s meteors is now " + ChatColor.LIGHT_PURPLE + team.getMeteors() + ChatColor.YELLOW + ".");
    }

}