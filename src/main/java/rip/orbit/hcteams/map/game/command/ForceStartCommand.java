package rip.orbit.hcteams.map.game.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.Game;
import rip.orbit.hcteams.map.game.GameState;

public class ForceStartCommand {

    @Command(names = { "game forcestart" }, description = "Force start an event", permission = "op")
    public static void execute(Player player) {
        if (!HCF.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED.toString() + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.getState() == GameState.WAITING) {
            ongoingGame.forceStart();
        } else {
            player.sendMessage(ChatColor.RED + "Can't force start an event that has already been started.");
        }
    }

}
