package rip.orbit.hcteams.team;

import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.util.UUIDUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TeamType implements ParameterType<Team> {

    @Override
	public Team transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = HCF.getInstance().getTeamHandler().getTeam(((Player) sender).getUniqueId());

            if (team == null) {
                sender.sendMessage(ChatColor.GRAY + "You're not on a team!");
                return (null);
            }

            return (team);
        }

        Team byName = HCF.getInstance().getTeamHandler().getTeam(source);

        if (byName != null) {
            return (byName);
        }


        Player bukkitPlayer = HCF.getInstance().getServer().getPlayer(source);

        if (bukkitPlayer != null) {
            Team byMemberBukkitPlayer = HCF.getInstance().getTeamHandler().getTeam(bukkitPlayer.getUniqueId());

            if (byMemberBukkitPlayer != null) {
                return (byMemberBukkitPlayer);
            }
        }

        Team byMemberUUID = HCF.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(source));

        if (byMemberUUID != null) {
            return (byMemberUUID);
        }

        sender.sendMessage(ChatColor.RED + "No team or member with the name " + source + " found.");
        return (null);
    }

    @Override
	public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        // Teams being included in the completion is ENABLED by default.
        if (!flags.contains("noteams")) {
            for (Team team : HCF.getInstance().getTeamHandler().getTeams()) {
                if (StringUtils.startsWithIgnoreCase(team.getName(), source)) {
                    completions.add(team.getName());
                }
            }
        }

        // Players being included in the completion is DISABLED by default.
        if (flags.contains("players")) {
            for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                    completions.add(player.getName());
                }
            }
        }

        return (completions);
    }

}