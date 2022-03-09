package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.team.Team;

public class TeamTPCommand {

    @Command(names={ "team tp", "t tp", "f tp", "faction tp", "fac tp" }, permission="foxtrot.factiontp")
    public static void teamTP(Player sender, @Param(name="team", defaultValue="self") Team team) {
        if (team.getHq() != null) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
            sender.teleport(team.getHq());
        } else if (team.getClaims().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s claim.");
            sender.teleport(team.getClaims().get(0).getMaximumPoint().add(0, 100, 0));
        } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " doesn't have a HQ or any claims.");
        }
    }

}
