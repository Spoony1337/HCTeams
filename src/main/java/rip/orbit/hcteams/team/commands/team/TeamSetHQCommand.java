package rip.orbit.hcteams.team.commands.team;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.LandBoard;

import java.awt.*;

public class TeamSetHQCommand {

    @Command(names={ "team sethq", "t sethq", "f sethq", "faction sethq", "fac sethq", "team sethome", "t sethome", "f sethome", "faction sethome", "fac sethome", "sethome", "sethq" }, permission="")
    public static void teamSetHQ(Player sender) {
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
            if (LandBoard.getInstance().getTeam(sender.getLocation()) != team) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You can only set HQ in your team's territory.");
                    return;
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Setting HQ outside of your team's territory would normally be disallowed, but this check is being bypassed due to your rank.");
                }
            }

            team.setHq(sender.getLocation());
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's HQ point!");

            HCF.getInstance().getTeamHandler().getTeams().forEach(targetTeam -> {
                if (targetTeam.getFactionFocused() != null) {
                    if (targetTeam.getFactionFocused().getName().equalsIgnoreCase(team.getName())) {
                        if (targetTeam.getFactionFocused() != null) {
                            if (targetTeam.getFactionFocused().getHq() != null) {
                                LCWaypoint waypoint = new LCWaypoint(targetTeam.getFactionFocused().getName() + "'s HQ", targetTeam.getFactionFocused().getHq(), Color.orange.hashCode(), true);
                                targetTeam.getOnlineMembers().forEach(m -> {
                                    LunarClientAPI.getInstance().removeWaypoint(m, waypoint);
                                });
                            }
                        }

                        if (targetTeam.getHq() != null) {
                            LCWaypoint waypoint = new LCWaypoint(targetTeam.getFactionFocused().getName() + "'s HQ", targetTeam.getFactionFocused().getHq(), Color.orange.hashCode(), true);
                            targetTeam.getOnlineMembers().forEach(m -> {
                                LunarClientAPI.getInstance().sendWaypoint(m, waypoint);
                            });
                        }
                    }
                }
            });
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}