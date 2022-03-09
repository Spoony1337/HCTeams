package rip.orbit.hcteams.events.koth.commands.koth;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.events.Event;

public class KOTHHiddenCommand {

    @Command(names={ "KOTH Hidden", "events hidden", "event hidden" }, permission="foxtrot.koth.admin")
    public static void kothHidden(Player sender, @Param(name="koth") Event koth, @Param(name="hidden") boolean hidden) {
        koth.setHidden(hidden);
        sender.sendMessage(ChatColor.GRAY + "Set visibility for the " + koth.getName() + " event.");
    }

}