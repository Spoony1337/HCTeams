package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class ReloadMapConfigCommand {

    @Command(names={ "reloadMapConfig" }, permission="op")
    public static void reloadMapConfig(Player sender) {
        HCF.getInstance().reloadConfig();
        HCF.getInstance().getMapHandler().reloadConfig();
        sender.sendMessage(ChatColor.DARK_PURPLE + "Reloaded mapInfo.json from file.");
    }

}