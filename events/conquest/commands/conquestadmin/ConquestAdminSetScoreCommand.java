package rip.orbit.hcteams.events.conquest.commands.conquestadmin;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.conquest.ConquestHandler;
import rip.orbit.hcteams.events.conquest.game.ConquestGame;
import rip.orbit.hcteams.team.Team;

public class ConquestAdminSetScoreCommand {

    @Command(names={ "conquestadmin setscore" }, permission="op")
    public static void conquestAdminSetScore(CommandSender sender, @Param(name="team") Team team, @Param(name="score") int score) {
        ConquestGame game = HCF.getInstance().getConquestHandler().getGame();

        if (game == null) {
            sender.sendMessage(ChatColor.RED + "Conquest is not active.");
            return;
        }

        game.getTeamPoints().put(team.getUniqueId(), score);
        sender.sendMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Updated the score for " + team.getName() + ChatColor.GOLD + ".");
    }

}