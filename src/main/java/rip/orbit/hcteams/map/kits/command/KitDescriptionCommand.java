package rip.orbit.hcteams.map.kits.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.kits.DefaultKit;

public class KitDescriptionCommand {

    @Command(names = { "kitadmin setdesc" }, description = "Sets the description of a kit", permission = "op")
    public static void execute(Player player, @Param(name = "kit") DefaultKit kit, @Param(name = "description", wildcard = true) String description) {
        kit.setDescription(description);
        HCF.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set description of " + kit.getName() + "!");
    }

}
