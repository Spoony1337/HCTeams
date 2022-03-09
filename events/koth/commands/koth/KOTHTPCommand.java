package rip.orbit.hcteams.events.koth.commands.koth;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventType;
import rip.orbit.hcteams.events.koth.KOTH;

public class KOTHTPCommand {

    @Command(names={ "KOTH TP", "KOTHTP", "events tp", "event tp" }, permission="foxtrot.koth")
    public static void kothTP(Player sender, @Param(name="koth", defaultValue="active") Event koth) {
        if (koth.getType() == EventType.KOTH) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(HCF.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GRAY + "Teleported to the " + koth.getName() + " KOTH.");
        } else if (koth.getType() == EventType.DTC) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(HCF.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GRAY + "Teleported to the " + koth.getName() + " DTC.");
        }

        sender.sendMessage(ChatColor.RED + "You can't TP to an event that doesn't have a location.");
    }

}