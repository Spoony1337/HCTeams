package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class ToggleDeathMessagesCommand {

    @Command(names = {"toggledeathmessages", "tdm"}, permission = "")
    public static void toggledeathmessages(Player sender) {
        boolean val = !HCF.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see Death Messages!");
        HCF.getInstance().getToggleDeathMessageMap().setDeathMessagesEnabled(sender.getUniqueId(), val);
    }

}
