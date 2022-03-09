package rip.orbit.hcteams.map.kits.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.kits.DefaultKit;

public class KitOrderCommand {

    @Command(names = { "kitadmin setorder" }, description = "Sets the order of a kit", permission = "op")
    public static void execute(Player player, @Param(name = "kit") DefaultKit kit, @Param(name = "order") int order) {
        kit.setOrder(order);
        HCF.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set order of " + kit.getName() + " to " + order + "!");
    }

}
