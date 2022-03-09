package rip.orbit.hcteams.events.koth.commands.kothschedule;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;

public class KOTHDisableSchedule {

    @Command(names = "KOTHSchedule Disable", permission = "foxtrot.koth.admin")
    public static void kothScheduleDisable(CommandSender sender) {
        HCF.getInstance().getEventHandler().setScheduleEnabled(false);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.RED + "disabled" + ChatColor.YELLOW + "");
    }

}
