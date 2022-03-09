package rip.orbit.hcteams.events.citadel.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.citadel.CitadelHandler;

public class CitadelRespawnChestsCommand {

    @Command(names={"citadel respawnchests"}, permission="op")
    public static void citadelRespawnChests(Player sender) {
        HCF.getInstance().getCitadelHandler().respawnCitadelChests();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Respawned all Citadel chests.");
    }

}