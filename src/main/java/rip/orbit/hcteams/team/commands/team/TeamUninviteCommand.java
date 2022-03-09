package rip.orbit.hcteams.team.commands.team;

import com.google.common.collect.ImmutableMap;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.track.TeamActionTracker;
import rip.orbit.hcteams.team.track.TeamActionType;

import java.util.UUID;

public class TeamUninviteCommand {

    @Command(names={ "team uninvite", "t uninvite", "f uninvite", "faction uninvite", "fac uninvite", "team revoke", "t revoke", "f revoke", "faction revoke", "fac revoke" }, permission="")
    public static void teamUninvite(Player sender, @Param(name="all | player") String allPlayer) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You need to be in a team to do this.");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (allPlayer.equalsIgnoreCase("all")) {
                team.getInvitations().clear();
                sender.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(HCF.getInstance(), new BukkitRunnable() {
                    @Override
					public void run() {
                        UUID nameUUID = UUIDUtils.uuid(allPlayer);

                        Bukkit.getScheduler().runTaskAsynchronously(HCF.getInstance(), new BukkitRunnable() {

                            @Override
							public void run() {
                                if (team.getInvitations().remove(nameUUID)) {
                                    TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_INVITE_REVOKED, ImmutableMap.of(
                                            "playerId", allPlayer,
                                            "uninvitedById", sender.getUniqueId(),
                                            "uninvitedByName", sender.getName()
                                    ));

                                    team.getInvitations().remove(nameUUID);
                                    team.flagForSave();
                                    sender.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + allPlayer + "!");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "No pending invitation for '" + allPlayer + "'!");
                                }
                            }

                        });
                    }

                });
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}