package rip.orbit.hcteams.team.commands.team;

import com.google.common.collect.ImmutableMap;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.track.TeamActionTracker;
import rip.orbit.hcteams.team.track.TeamActionType;
import rip.orbit.hcteams.util.CC;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TeamUnShadowMuteCommand {

    @Command(names={ "team unshadowmute", "t unshadowmute", "f unshadowmute", "faction unshadowmute", "fac unshadowmute" }, permission="foxtrot.mutefaction")
    public static void teamUnShadowMute(Player sender, @Param(name = "team") Team team) {
        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                "shadowMute", "true"
        ));

        Iterator<Map.Entry<UUID, String>> mutesIterator = TeamShadowMuteCommand.getTeamShadowMutes().entrySet().iterator();

        while (mutesIterator.hasNext()) {
            Map.Entry<UUID, String> mute = mutesIterator.next();

            if (mute.getValue().equalsIgnoreCase(team.getName())) {
                mutesIterator.remove();
            }
        }

        sender.sendMessage(CC.translate("§eUn-shadowmuted the team §9" + team.getName() + "§e."));
    }

}