package rip.orbit.hcteams.map.kits.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.kits.Kit;

public class KitsCreateCommand {

    @Command(names = { "kitadmin create" }, permission = "op")
    public static void execute(Player sender, @Param(name = "name", wildcard = true) String name) {
        if (HCF.getInstance().getMapHandler().getKitManager().getDefaultKit(name) != null) {
            sender.sendMessage(ChatColor.RED + "That kit already exists.");
            return;
        }
            
        Kit kit = HCF.getInstance().getMapHandler().getKitManager().getOrCreateDefaultKit(name);
        kit.update(sender.getInventory());

        HCF.getInstance().getMapHandler().getKitManager().saveDefaultKits();
        
        sender.sendMessage(ChatColor.YELLOW + "The " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + " kit has been created from your inventory.");
    }

}
