package rip.orbit.hcteams.events.koth.commands.kothschedule;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class KOTHReloadSchedule {

    @Command(names={ "KOTHSchedule Reload" }, permission="foxtrot.koth.admin")
    public static void kothScheduleReload(Player sender) {
        HCF.getInstance().getEventHandler().loadSchedules();
        sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Reloaded the KOTH schedule.");
    }

}