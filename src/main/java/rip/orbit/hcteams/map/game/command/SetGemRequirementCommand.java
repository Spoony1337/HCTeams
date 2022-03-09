package rip.orbit.hcteams.map.game.command;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.map.game.Game;

public class SetGemRequirementCommand {

    @Command(names = { "game setstarsrequired", "game setstarreq" }, description = "Set the ongoing game's star requirement", permission = "op")
    public static void execute(Player player) {
        if (HCF.getInstance().getMapHandler().getGameHandler() != null) {
            Game game = HCF.getInstance().getMapHandler().getGameHandler().getOngoingGame();
            if (game != null) {
                game.setStarRequiredToJoin(!game.isStarRequiredToJoin());
                player.sendMessage(ChatColor.GREEN + "Star requirement has been " + ChatColor.WHITE + (game.isStarRequiredToJoin() ? "enabled" : "disabled") + ChatColor.GREEN + "!");
            } else {
                player.sendMessage(ChatColor.RED + "There is no ongoing game.");
            }
        }
    }

}
