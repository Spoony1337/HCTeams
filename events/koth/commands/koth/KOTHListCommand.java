package rip.orbit.hcteams.events.koth.commands.koth;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventType;
import rip.orbit.hcteams.events.dtc.DTC;
import rip.orbit.hcteams.events.koth.KOTH;

import static org.bukkit.ChatColor.*;

public class KOTHListCommand {
    
    @Command(names = { "KOTH List", "events list", "event list" }, permission = "foxtrot.koth")
    public static void kothList(Player sender) {
        if (HCF.getInstance().getEventHandler().getEvents().isEmpty()) {
            sender.sendMessage(RED + "There aren't any events set.");
            return;
        }
        
        for (Event event : HCF.getInstance().getEventHandler().getEvents()) {
            if (event.getType() == EventType.KOTH) {
                KOTH koth = (KOTH) event;
                sender.sendMessage((koth.isHidden() ? DARK_GRAY + "[H] " : "") + (koth.isActive() ? GREEN : RED) + koth.getName() + WHITE + " - " + GRAY + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()) + DARK_GRAY + "/" + GRAY + TimeUtils.formatIntoMMSS(koth.getCapTime()) + " " + WHITE + "- " + GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()));
            } else if (event.getType() == EventType.DTC) {
                DTC dtc = (DTC) event;
                sender.sendMessage((dtc.isHidden() ? DARK_GRAY + "[H] " : "") + (dtc.isActive() ? GREEN : RED) + dtc.getName() + WHITE + " - " + GRAY + "P: " + dtc.getCurrentPoints());
            }
        }
    }
    
}