package rip.orbit.hcteams.events.citadel.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.citadel.CitadelHandler;
import rip.orbit.hcteams.util.CC;

public class CitadelSaveLoottableCommand {

    @Command(names = "citadel info", permission = "")
    public static void info(CommandSender sender) {

        sender.sendMessage(CC.translate("&6&lCitadel Information"));
        sender.sendMessage(CC.translate(" "));
        if (HCF.getInstance().getCitadelHandler().getCappers().stream().findFirst().isPresent()) {
            sender.sendMessage(CC.translate("&dCapper &7- " + HCF.getInstance().getTeamHandler().getTeam(HCF.getInstance().getCitadelHandler().getCappers().stream().findFirst().get()).getName()));
        } else {
            sender.sendMessage(CC.translate("&dCapper &7- No one"));

        }

    }

    @Command(names={"citadel saveloottable"}, permission="op")
    public static void citadelSaveLoottable(Player sender) {
        HCF.getInstance().getCitadelHandler().getCitadelLoot().clear();

        for (ItemStack itemStack : sender.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                HCF.getInstance().getCitadelHandler().getCitadelLoot().add(itemStack);
            }
        }

        HCF.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel loot from your inventory.");
    }

}