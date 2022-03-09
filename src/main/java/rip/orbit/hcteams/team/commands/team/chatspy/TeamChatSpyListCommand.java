package rip.orbit.hcteams.team.commands.team.chatspy;

import net.frozenorb.qlib.command.Command;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

public class TeamChatSpyListCommand {

    @Command(names={ "team chatspy list", "t chatspy list", "f chatspy list", "faction chatspy list", "fac chatspy list" }, permission="foxtrot.chatspy")
    public static void teamChatSpyList(Player sender) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ObjectId team : HCF.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId())) {
            Team teamObj = HCF.getInstance().getTeamHandler().getTeam(team);

            if (teamObj != null) {
                stringBuilder.append(ChatColor.YELLOW).append(teamObj.getName()).append(ChatColor.GOLD).append(", ");
            }
        }

        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "You are currently spying on the team chat of: " + stringBuilder.toString());
    }

}