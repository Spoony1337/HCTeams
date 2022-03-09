package rip.orbit.hcteams.events.citadel.commands;

import com.google.common.base.Joiner;
import net.frozenorb.qlib.command.Command;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.citadel.CitadelHandler;
import rip.orbit.hcteams.team.Team;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CitadelCommand {

    // Make this pretty.
    @Command(names={ "citadel" }, permission="")
    public static void citadel(Player sender) {
        Set<ObjectId> cappers = HCF.getInstance().getCitadelHandler().getCappers();
        Set<String> capperNames = new HashSet<>();

        for (ObjectId capper : cappers) {
            Team capperTeam = HCF.getInstance().getTeamHandler().getTeam(capper);

            if (capperTeam != null) {
                capperNames.add(capperTeam.getName());
            }
        }

        if (!capperNames.isEmpty()) {
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was captured by " + ChatColor.GREEN + Joiner.on(", ").join(capperNames) + ChatColor.YELLOW + "");
        } else {
            Event citadel = HCF.getInstance().getEventHandler().getEvent("Citadel");

            if (citadel != null && citadel.isActive()) {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was not captured last week.");
            }
        }

        Date lootable = HCF.getInstance().getCitadelHandler().getLootable();
        sender.sendMessage(ChatColor.GOLD + "Citadel: " + ChatColor.WHITE + "Lootable " + (lootable.before(new Date()) ? "now" : "at " + (new SimpleDateFormat()).format(lootable) + (capperNames.isEmpty() ? "" : ", and lootable now by " + Joiner.on(", ").join(capperNames) + ".")));
    }

}