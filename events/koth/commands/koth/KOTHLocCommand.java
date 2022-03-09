package rip.orbit.hcteams.events.koth.commands.koth;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventType;
import rip.orbit.hcteams.events.koth.KOTH;

public class KOTHLocCommand {

    @Command(names={ "KOTH loc" }, permission="foxtrot.koth.admin")
    public static void kothLoc(Player sender, @Param(name="koth") Event koth) {
        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Unable to set location for a non-KOTH event.");
        } else {
            ((KOTH) koth).setLocation(sender.getLocation());
            sender.sendMessage(ChatColor.GRAY + "Set cap location for the " + koth.getName() + " KOTH.");
        }
    }

}