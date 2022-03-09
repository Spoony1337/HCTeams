package rip.orbit.hcteams.map.kits.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.kits.DefaultKit;

public class KitIconCommand {

    @Command(names = { "kitadmin seticon" }, description = "Sets the icon of a kit", permission = "op")
    public static void execute(Player player, @Param(name = "kit") DefaultKit kit) {
        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "You have no item in your hand!");
            return;
        }

        kit.setIcon(player.getItemInHand());
        HCF.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set icon of " + kit.getName() + "!");
    }

}
