package rip.orbit.hcteams.team.commands.pvp;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class PvPTimeCommand {

    @Command(names={ "pvptimer time", "pvp time" }, permission="")
    public static void pvpTime(Player sender) {
        if (HCF.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You have " + TimeUtils.formatIntoMMSS(HCF.getInstance().getPvPTimerMap().getSecondsRemaining(sender.getUniqueId())) + " left on your PVP Timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
        }
    }

}