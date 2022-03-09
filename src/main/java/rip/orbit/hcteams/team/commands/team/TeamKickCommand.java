package rip.orbit.hcteams.team.commands.team;

import com.google.common.collect.ImmutableMap;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.track.TeamActionTracker;
import rip.orbit.hcteams.team.track.TeamActionType;

import java.util.UUID;

public class TeamKickCommand {

    @Command(names = {"team kick", "t kick", "f kick", "faction kick", "fac kick"}, permission = "")
    public static void teamKick(Player sender, @Param(name = "player") UUID player) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You need to be in a team to do this.");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't on your team!");
            return;
        }

        if (team.isOwner(player)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if(team.isCoLeader(player) && (!team.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
            return;
        }

        if (team.isCaptain(player) && !team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        Player bukkitPlayer = HCF.getInstance().getServer().getPlayer(player);

        if (SpawnTagHandler.isTagged(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + bukkitPlayer.getName() + " is currently combat-tagged! You can forcibly kick " + bukkitPlayer.getName() + " by using '"
                    + ChatColor.YELLOW + "/f forcekick " + bukkitPlayer.getName() + ChatColor.RED + "' which will cost your team 1 DTR.");
            return;
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " was kicked by " + sender.getName() + "!");

        TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "false"
        ));

        if (team.removeMember(player)) {
            team.disband();
        } else {
            team.flagForSave();
        }

        HCF.getInstance().getTeamHandler().setTeam(player, null);

//        if (bukkitPlayer != null) {
//            FrozenNametagHandler.reloadPlayer(bukkitPlayer);
//            FrozenNametagHandler.reloadOthersFor(bukkitPlayer);
//        }
    }

}