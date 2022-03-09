package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.server.ServerHandler;

public class LogoutCommand {

    @Command(names={ "Logout" }, permission="")
    public static void logout(Player sender) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You can't log out while you're frozen!");
            return;
        }

        if(ServerHandler.getTasks().containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You are already logging out.");
            return; // dont potato and let them spam logouts
        }

        HCF.getInstance().getServerHandler().startLogoutSequence(sender);
    }

}