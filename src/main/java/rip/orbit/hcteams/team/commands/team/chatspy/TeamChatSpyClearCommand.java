package rip.orbit.hcteams.team.commands.team.chatspy;

import net.frozenorb.qlib.command.Command;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

import java.util.ArrayList;

public class TeamChatSpyClearCommand {

    @Command(names={ "team chatspy clear", "t chatspy clear", "f chatspy clear", "faction chatspy clear", "fac chatspy clear" }, permission="foxtrot.chatspy")
    public static void teamChatSpyClear(Player sender) {
        HCF.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), new ArrayList<>());
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on any teams.");
    }

}