package rip.orbit.hcteams.commands;

import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.team.track.TeamActionTracker;

public class ToggleDatabaseTeamLog {

    @Command(names = {"toggledatabaseteamlog" }, permission = "op")
    public static void toggleDatabaseTeamLog(Player sender) {
        TeamActionTracker.setDatabaseLogEnabled(!TeamActionTracker.isDatabaseLogEnabled());
        sender.sendMessage("Enabled: " + TeamActionTracker.isDatabaseLogEnabled());
    }

}