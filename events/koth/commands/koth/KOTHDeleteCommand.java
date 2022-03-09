package rip.orbit.hcteams.events.koth.commands.koth;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;

public class KOTHDeleteCommand {

    @Command(names={ "KOTH Delete", "events delete", "event delete" }, permission="foxtrot.koth.admin")
    public static void kothDelete(Player sender, @Param(name="koth") Event koth) {
        HCF.getInstance().getEventHandler().getEvents().remove(koth);
        HCF.getInstance().getEventHandler().saveEvents();
        sender.sendMessage(ChatColor.GRAY + "Deleted event " + koth.getName() + "");
    }

}