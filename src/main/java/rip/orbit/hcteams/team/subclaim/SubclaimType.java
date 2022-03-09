package rip.orbit.hcteams.team.subclaim;

import net.frozenorb.qlib.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.claims.Subclaim;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SubclaimType implements ParameterType<Subclaim> {

    @Override
	public Subclaim transform(CommandSender sender, String source) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, players only. :/");
            return (null);
        }

        Player player = (Player) sender;
        Team team = HCF.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return (null);
        }

        if (source.equals("location")) {
            Subclaim subclaim = team.getSubclaim(player.getLocation());

            if (subclaim == null) {
                sender.sendMessage(ChatColor.RED + "You are not inside of a subclaim.");
                return (null);
            }

            return (subclaim);
        }

        Subclaim subclaim = team.getSubclaim(source);

        if (subclaim == null) {
            sender.sendMessage(ChatColor.RED + "No subclaim with the name " + source + " found.");
            return (null);
        }

        return (subclaim);
    }

    @Override
	public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();
        Team team = HCF.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            return (completions);
        }


        for (Subclaim subclaim : team.getSubclaims()) {
            if (StringUtils.startsWithIgnoreCase(subclaim.getName(), source)) {
                completions.add(subclaim.getName());
            }
        }

        return (completions);
    }


}