package rip.orbit.hcteams.events.citadel.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.citadel.CitadelHandler;

public class CitadelRescanChestsCommand {

    @Command(names={"citadel rescanchests"}, permission="op")
    public static void citadelRescanChests(Player sender) {
        HCF.getInstance().getCitadelHandler().scanLoot();
        HCF.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Rescanned all Citadel chests.");
    }

}