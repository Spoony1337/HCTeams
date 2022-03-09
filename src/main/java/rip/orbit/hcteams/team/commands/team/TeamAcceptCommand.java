package rip.orbit.hcteams.team.commands.team;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.gravity.profile.global.pastfaction.PastFaction;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.server.SpawnTagHandler;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.dtr.DTRHandler;
import rip.orbit.hcteams.team.event.FullTeamBypassEvent;

public class TeamAcceptCommand {

    @Command(names = {"team accept", "t accept", "f accept", "faction accept", "fac accept", "team a", "t a", "f a", "faction a", "fac a", "team join", "t join", "f join", "faction join", "fac join", "team j", "t j", "f j", "faction j", "fac j"}, permission = "")
    public static void teamAccept(Player sender, @Param(name = "team") Team team) {
        if (HCF.getInstance().getDeathbanMap().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You can't do this while you are deathbanned.");
            return;
        }

        if (team.getInvitations().contains(sender.getUniqueId())) {
            if (HCF.getInstance().getTeamHandler().getTeam(sender) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return;
            }

            if (team.getMembers().size() >= HCF.getInstance().getMapHandler().getTeamSize()) {
                FullTeamBypassEvent attemptEvent = new FullTeamBypassEvent(sender, team);
                HCF.getInstance().getServer().getPluginManager().callEvent(attemptEvent);

                if (!attemptEvent.isAllowBypass()) {
                    sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team is full!");
                    return;
                }
            }

            if (DTRHandler.isOnCooldown(team) && !HCF.getInstance().getServerHandler().isPreEOTW() && !HCF.getInstance().getMapHandler().isKitMap() && !HCF.getInstance().getServerHandler().isVeltKitMap()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team not regenerating DTR!");
                return;
            }

            if (team.getMembers().size() >= 15 && HCF.getInstance().getTeamHandler().isRostersLocked()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team rosters are locked server-wide!");
                return;
            }

            if (SpawnTagHandler.isTagged(sender)) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: You are combat tagged!");
                return;
            }

            team.getInvitations().remove(sender.getUniqueId());
            team.addMember(sender.getUniqueId());
            HCF.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), team);

            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has joined the team!");
            Profile profile = Profile.getByUuid(sender.getUniqueId());
            if (!profile.getGlobalInfo().getPastFactions().contains(profile.pfByName(team.getName()))) {
                profile.getGlobalInfo().getPastFactions().add(new PastFaction(team.getName()));
            }

        } else {
            sender.sendMessage(ChatColor.RED + "This team has not invited you!");
        }
    }

}