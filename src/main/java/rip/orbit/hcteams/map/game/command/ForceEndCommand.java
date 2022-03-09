package rip.orbit.hcteams.map.game.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

public class ForceEndCommand {

    @Command(names = { "game forceend" }, description = "Force end an event", permission = "op")
    public static void execute(Player player) {
        if (!HCF.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED.toString() + "There is no ongoing event.");
            return;
        }

        HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame().endGame();
        player.sendMessage(ChatColor.GREEN + "Successfully ended the ongoing event!");
    }

}
