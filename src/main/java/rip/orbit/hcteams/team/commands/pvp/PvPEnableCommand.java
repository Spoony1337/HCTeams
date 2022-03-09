package rip.orbit.hcteams.team.commands.pvp;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class PvPEnableCommand {

    @Command(names={ "pvptimer enable", "pvp enable", "pvptimer remove", "pvp remove" }, permission="")
    public static void pvpEnable(Player sender, @Param(name="target", defaultValue="self") Player target) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        if (target != sender && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        if (HCF.getInstance().getPvPTimerMap().hasTimer(target.getUniqueId())) {
            HCF.getInstance().getPvPTimerMap().removeTimer(target.getUniqueId());

            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "Your PvP Timer has been removed!");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + "'s PvP Timer has been removed!");
            }
        } else {
            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "You do not have a PvP Timer!");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " does not have a PvP Timer.");
            }
        }
    }

}