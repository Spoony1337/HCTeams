package rip.orbit.hcteams.team.commands.team;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.track.TeamActionTracker;
import rip.orbit.hcteams.team.track.TeamActionType;
import rip.orbit.hcteams.util.CC;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TeamShadowMuteCommand {

    @Getter public static Map<UUID, String> teamShadowMutes = new HashMap<>();

    @Command(names={ "team shadowmute", "t shadowmute", "f shadowmute", "faction shadowmute", "fac shadowmute" }, permission="foxtrot.mutefaction")
    public static void teamShadowMute(Player sender, @Param(name = "team") Team team, @Param(name = "time") int time) {
        int timeSeconds = time * 60;

        for (UUID player : team.getMembers()) {
            teamShadowMutes.put(player, team.getName());
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "true",
                "mutedById", sender.getUniqueId(),
                "mutedByName", sender.getName(),
                "duration", time
        ));

        new BukkitRunnable() {
            @Override
			public void run() {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                        "shadowMute", "true"
                ));

                Iterator<java.util.Map.Entry<UUID, String>> mutesIterator = teamShadowMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    java.util.Map.Entry<UUID, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(team.getName())) {
                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLaterAsynchronously(HCF.getInstance(), timeSeconds * 20L);

        sender.sendMessage(CC.translate("§eShadow muted the team §9" + team.getName() +" §efor §7" + TimeUtils.formatIntoMMSS(timeSeconds) + "§e."));
    }

}