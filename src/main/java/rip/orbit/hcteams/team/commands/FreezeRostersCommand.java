package rip.orbit.hcteams.team.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.TeamHandler;

public class FreezeRostersCommand {

    @Command(names={ "freezerosters" }, permission="op")
    public static void freezeRosters(Player sender) {
        TeamHandler teamHandler = HCF.getInstance().getTeamHandler();
        teamHandler.setRostersLocked(!teamHandler.isRostersLocked());

        sender.sendMessage(ChatColor.YELLOW + "Team rosters are now " + ChatColor.LIGHT_PURPLE + (teamHandler.isRostersLocked() ? "locked" : "unlocked") + ChatColor.YELLOW + ".");
    }

}