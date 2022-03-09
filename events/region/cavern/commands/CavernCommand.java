package rip.orbit.hcteams.events.region.cavern.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.region.cavern.Cavern;
import rip.orbit.hcteams.events.region.cavern.CavernHandler;
import rip.orbit.hcteams.team.Team;

import static org.bukkit.ChatColor.*;

public class CavernCommand {

    @Command(names = "cavern scan", permission = "op")
    public static void cavernScan(Player sender) {
        if (!HCF.getInstance().getConfig().getBoolean("cavern", false)) {
            sender.sendMessage(RED + "Cavern is currently disabled. Check config.yml to toggle.");
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(CavernHandler.getCavernTeamName());

        // Make sure we have a team
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must first create the team (" + CavernHandler.getCavernTeamName() + ") and claim it!");
            return;
        }

        // Make sure said team has a claim
        if (team.getClaims().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You must claim land for '" + CavernHandler.getCavernTeamName() + "' before scanning it!");
            return;
        }

        // We have a claim, and a team, now do we have a glowstone?
        if (!HCF.getInstance().getCavernHandler().hasCavern()) {
            HCF.getInstance().getCavernHandler().setCavern(new Cavern());
        }

        // We have a glowstone now, we're gonna scan and save the area
        HCF.getInstance().getCavernHandler().getCavern().scan();
        HCF.getInstance().getCavernHandler().save(); // save to file :D

        sender.sendMessage(GREEN + "[Cavern] Scanned all ores and saved Cavern to file!");
    }

    @Command(names = "cavern reset", permission = "op")
    public static void cavernReset(Player sender) {
        Team team = HCF.getInstance().getTeamHandler().getTeam(CavernHandler.getCavernTeamName());

        // Make sure we have a team, claims, and a mountain!
        if (team == null || team.getClaims().isEmpty() || !HCF.getInstance().getCavernHandler().hasCavern()) {
            sender.sendMessage(RED + "Create the team '" + CavernHandler.getCavernTeamName() + "', then make a claim for it, finally scan it! (/cavern scan)");
            return;
        }

        // Check, check, check, LIFT OFF! (reset the mountain)
        HCF.getInstance().getCavernHandler().getCavern().reset();

        Bukkit.broadcastMessage(AQUA + "[Cavern]" + GREEN + " All ores have been reset!");
    }
}