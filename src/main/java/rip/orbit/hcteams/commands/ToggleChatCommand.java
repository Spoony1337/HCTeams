package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class ToggleChatCommand {

    @Command(names={ "ToggleChat", "ToggleGlobalChat", "TGC" }, permission="")
    public static void toggleChat(Player sender) {
        boolean val = !HCF.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see global chat!");
        HCF.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(sender.getUniqueId(), val);
    }

}