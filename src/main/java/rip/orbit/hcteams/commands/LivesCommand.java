package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;


public class LivesCommand {

    @Command(names={ "lives" }, permission="")
    public static void lives(CommandSender commandSender) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Bad console.");
            return;
        }

        Player sender = (Player) commandSender;
        
        int lives = HCF.getInstance().getLivesMap().getLives(sender.getUniqueId());
        sender.sendMessage(ChatColor.YELLOW + "Lives are used to revive you instantly upon death. You can purchase more lives at: store.orbit.rip");
        sender.sendMessage(ChatColor.YELLOW + "Lives: " + ChatColor.RED + lives);
    }
}
