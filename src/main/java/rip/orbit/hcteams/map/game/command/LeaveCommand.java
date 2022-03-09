package rip.orbit.hcteams.map.game.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.Game;

public class LeaveCommand {

    @Command(names = { "leave", "game leave" }, description = "Leave the event", permission = "")
    public static void execute(Player player) {
        if (!HCF.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (!ongoingGame.isPlaying(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not in the ongoing event!");
            return;
        }

        ongoingGame.removePlayer(player);
    }

}
