package rip.orbit.hcteams.events.conquest.commands.conquestadmin;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.conquest.game.ConquestGame;

public class ConquestAdminStartCommand {

    @Command(names={ "conquestadmin start" }, permission="op")
    public static void conquestAdminStart(CommandSender sender) {
        ConquestGame game = HCF.getInstance().getConquestHandler().getGame();

        if (game != null) {
            sender.sendMessage(ChatColor.RED + "Conquest is already active.");
            return;
        }

        new ConquestGame();
    }

}