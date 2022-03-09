package rip.orbit.hcteams.nametag;

import com.cheatbreaker.api.CheatBreakerAPI;
import com.lunarclient.bukkitapi.LunarClientAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;

import java.util.stream.Collectors;


public class ClientListener implements Listener {

	public ClientListener() {
		Bukkit.getScheduler().runTaskTimer(HCF.getInstance(), () -> {
			double tps = Bukkit.spigot().getTPS()[1];
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				Bukkit.getOnlinePlayers().forEach(player -> CheatBreakerAPI.getInstance().overrideNametag(onlinePlayer, fetchNametag(onlinePlayer, player), player));
				Bukkit.getOnlinePlayers().forEach(player -> LunarClientAPI.getInstance().overrideNametag(onlinePlayer, fetchNametag(onlinePlayer, player), player));
			}
		}, 0, 40);
	}

	public Comparator<Team> compareFactionPoints = Comparator.comparingLong(Team::getPoints);

	public List<String> fetchNametag(Player target, Player viewer) {
		String nameTag = (target.has me? ChatColor.GRAY + "*" : "") + new FoxtrotNametagProvider().fetchNametag(target, viewer).getPrefix() + target.getName();
		List<String> tag = new ArrayList<>();
		if (!target.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			Team team = HCF.getInstance().getTeamHandler().getTeam(target);
			List<Team> Teams = HCF.getInstance().getTeamHandler().getTeams().stream().filter(Objects::nonNull).filter(x -> x.getPoints() > 0).distinct().sorted(compareFactionPoints).collect(Collectors.toList());
			Collections.reverse(Teams);
			if (team != null) {
				tag.add(ChatColor.GOLD + "[" + HCF.getInstance().getServerHandler().getDefaultRelationColor().toString() + team.getName(viewer) + CC.translate("&7 ") + team.getDTRWithColor() + team.getDTRSuffix() + ChatColor.GOLD + "]");
			}
			if (target.hasMetadata("invisible") && team == null) {
				tag.add(CC.translate("&7[Mod Mode]"));
			} else if (target.hasMetadata("invisible") && team != null) {
				tag.add(CC.translate("&7[Mod Mode]"));
			}
			if (Profile.getByUuid(target.getUniqueId()).getOptions().isFrozen() && team == null) {
				tag.add(CC.translate("&4&l[Frozen]"));
			} else if (Profile.getByUuid(target.getUniqueId()).getOptions().isFrozen() && team != null) {
				tag.add(CC.translate("&4&l[Frozen]"));
			}
		}
		tag.add(nameTag);
		return tag;
	}
}
