package rip.orbit.hcteams.events.koth.commands.koth;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventScheduledTime;
import rip.orbit.hcteams.events.koth.KOTH;

import java.util.Date;
import java.util.Map;

import static org.bukkit.ChatColor.*;

public class KOTHCommand {

    // Make this pretty.
    @Command(names={ "Event", "Event Next", "Event Info", "Event", "koth", "koth next", "koth info" }, permission="")
    public static void koth(Player sender) {
        for (Event koth : HCF.getInstance().getEventHandler().getEvents()) {
            if (!koth.isHidden() && koth.isActive()) {
                FancyMessage fm = new FancyMessage("[Events] ")
                        .color(GOLD)
                        .then(koth.getName())
                            .color(YELLOW) // koth name should be yellow
                            .style(UNDERLINE);
                            if (koth instanceof KOTH) {
                                fm.tooltip(YELLOW.toString() + ((KOTH) koth).getCapLocation().getBlockX() + ", " + ((KOTH) koth).getCapLocation().getBlockZ());
                            }
                            fm.color(YELLOW) // should color Event coords gray
                        .then(" can be contested now.")
                            .color(GOLD);
                        fm.send(sender);
                return;
            }
        }

        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : HCF.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            if (entry.getKey().toDate().after(now)) {
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + entry.getValue() + GOLD + " can be captured at " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + GOLD + "");
                sender.sendMessage(GOLD + "[KingOfTheHill] " + YELLOW + "It is currently " + BLUE + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(now) + GOLD + "");
                sender.sendMessage(YELLOW + "Type '/koth schedule' to see more upcoming Events.");
                return;
            }
        }

        sender.sendMessage(GOLD + "[KingOfTheHill] " + RED + "Next Event: " + YELLOW + "Undefined");
    }

}