package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

public class TeamOpenCommand {

    @Command(names={ "team open", "t open", "f open", "faction open", "fac open" }, permission="")
    public static void teamOpen(Player sender) {
        if (!(sender instanceof Player)) {
        sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
        return;
    }
     Team playerFaction = HCF.getInstance().getTeamHandler().getTeam(sender);
        if (playerFaction == null) {
        sender.sendMessage(ChatColor.RED + "You are not in a faction.");
        return;
    }
        if (!playerFaction.isOwner(sender.getUniqueId())) {
        sender.sendMessage(ChatColor.RED + "You must be a faction leader to do this.");
        return;
    }
        boolean newOpen = !playerFaction.isOpen();
        playerFaction.setOpen(newOpen);
        playerFaction.sendMessage(ChatColor.YELLOW + sender.getName() + " has " + (newOpen ? (ChatColor.GREEN + "opened") : (ChatColor.RED + "closed")) + ChatColor.YELLOW + " the faction to public.");
        return;
        }
    }
