package rip.orbit.hcteams.team.commands;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.team.Team;

public class StartDTRRegenCommand {

    @Command(names={ "startdtrregen" }, permission="foxtrot.startdtrregen")
    public static void startDTRRegen(Player sender, @Param(name="team") Team team) {
        team.setDTRCooldown(System.currentTimeMillis());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is now regenerating DTR.");
    }

}