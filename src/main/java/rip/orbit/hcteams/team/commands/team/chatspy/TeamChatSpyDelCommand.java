package rip.orbit.hcteams.team.commands.team.chatspy;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamChatSpyDelCommand {

    @Command(names={ "team chatspy del", "t chatspy del", "f chatspy del", "faction chatspy del", "fac chatspy del" }, permission="foxtrot.chatspy")
    public static void teamChatSpyDel(Player sender, @Param(name="team") Team team) {
        if (!HCF.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()).contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not spying on " + team.getName() + ".");
            return;
        }

        List<ObjectId> teams = new ArrayList<>(HCF.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()));

        teams.remove(team.getUniqueId());

        HCF.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on the chat of " + ChatColor.YELLOW + team.getName() + ChatColor.GREEN + ".");
    }

}