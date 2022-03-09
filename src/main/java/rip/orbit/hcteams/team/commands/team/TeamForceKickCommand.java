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
import rip.orbit.hcteams.team.dtr.DTRHandler;
import rip.orbit.hcteams.team.teamupgrades.enums.RegenUpgrades;
import rip.orbit.hcteams.team.track.TeamActionTracker;
import rip.orbit.hcteams.team.track.TeamActionType;
import rip.orbit.hcteams.util.JavaUtils;

import java.util.UUID;

public class TeamForceKickCommand {

    @Command(names = {"team forcekick", "t forcekick", "f forcekick", "faction forcekick", "fac forcekick"}, permission = "")
    public static void teamForceKick(Player sender, @Param(name = "player") UUID player) {
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

        if (team.isCaptain(player) && (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "true"
        ));

        if (team.removeMember(player)) {
            team.disband();
        } else {
            team.flagForSave();
        }

        HCF.getInstance().getTeamHandler().setTeam(player, null);
        Player bukkitPlayer = HCF.getInstance().getServer().getPlayer(player);

        if (SpawnTagHandler.isTagged(bukkitPlayer)) {
            team.setDTR(team.getDTR() - 1);
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " was force kicked by " + sender.getName() + " and your team lost 1 DTR!");
            long dtrCooldown;
            if (team.isRaidable()) {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
                dtrCooldown = System.currentTimeMillis() + HCF.getInstance().getMapHandler().getRegenTimeRaidable();
            } else {
                if (team.getRegenUpgrades() == RegenUpgrades.DEFAULT) {
                    dtrCooldown = System.currentTimeMillis() + HCF.getInstance().getMapHandler().getRegenTimeDeath();
                } else if (team.getRegenUpgrades() == RegenUpgrades.THIRTYFIVE) {
                    dtrCooldown = System.currentTimeMillis() + JavaUtils.parse("35m");
                } else if (team.getRegenUpgrades() == RegenUpgrades.THIRTY) {
                    dtrCooldown = System.currentTimeMillis() + JavaUtils.parse("30m");
                } else if (team.getRegenUpgrades() == RegenUpgrades.TWENTYFIVE) {
                    dtrCooldown = System.currentTimeMillis() + JavaUtils.parse("25m");
                } else {
                    dtrCooldown = System.currentTimeMillis() + HCF.getInstance().getMapHandler().getRegenTimeDeath();
                }
            }

            team.setDTRCooldown(dtrCooldown);
            DTRHandler.markOnDTRCooldown(team);
        } else {
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " was force kicked by " + sender.getName() + "!");
        }

//        if (bukkitPlayer != null) {
//            FrozenNametagHandler.reloadPlayer(bukkitPlayer);
//            FrozenNametagHandler.reloadOthersFor(bukkitPlayer);
//        }
    }

}