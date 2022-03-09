package rip.orbit.hcteams.map.game.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.Game;

public class JoinCommand {

    @Command(names = { "join", "game join" }, description = "Join an ongoing event", permission = "")
    public static void execute(Player player) {
        if (!HCF.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the event.");
            return;
        }

        if (HCF.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            HCF.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
        }

        if (HCF.getInstance().getStartingPvPTimerMap().get(player.getUniqueId())) {
            HCF.getInstance().getStartingPvPTimerMap().set(player.getUniqueId(), false);
        }

        try {
            ongoingGame.addPlayer(player);
        } catch (IllegalStateException e) {
            player.sendMessage(ChatColor.RED.toString() + e.getMessage());
        }
    }

}
