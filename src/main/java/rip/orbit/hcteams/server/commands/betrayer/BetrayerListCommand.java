package rip.orbit.hcteams.server.commands.betrayer;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.Betrayer;

public class BetrayerListCommand {

    @Command(names = {"betrayer list", "betrayers"}, permission = "")
    public static void betrayerList(Player sender) {
        StringBuilder betrayers = new StringBuilder();

        for (Betrayer betrayer : HCF.getInstance().getServerHandler().getBetrayers()) {
            betrayers.append(ChatColor.GRAY).append(UUIDUtils.name(betrayer.getUuid())).append(ChatColor.GOLD).append(", ");
        }

        if (betrayers.length() > 2) {
            betrayers.setLength(betrayers.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "Betrayers: " + betrayers.toString());
    }

}