package rip.orbit.hcteams.events.conquest.commands.conquestadmin;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.conquest.game.ConquestGame;

public class ConquestAdminStopCommand {

    @Command(names={ "conquestadmin stop" }, permission="op")
    public static void conquestAdminStop(CommandSender sender) {
        ConquestGame game = HCF.getInstance().getConquestHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Conquest is not active.");
            return;
        }

        game.endGame(null);
    }

}