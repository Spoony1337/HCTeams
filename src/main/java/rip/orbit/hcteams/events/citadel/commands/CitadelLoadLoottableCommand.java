package rip.orbit.hcteams.events.citadel.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.citadel.CitadelHandler;

public class CitadelLoadLoottableCommand {

    @Command(names={"citadel loadloottable"}, permission="op")
    public static void citadelLoadLoottable(Player sender) {
        sender.getInventory().clear();
        int itemIndex = 0;

        for (ItemStack itemStack : HCF.getInstance().getCitadelHandler().getCitadelLoot()) {
            sender.getInventory().setItem(itemIndex, itemStack);
            itemIndex++;
        }

        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Loaded Citadel loot into your inventory.");
    }

}