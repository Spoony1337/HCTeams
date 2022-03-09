package rip.orbit.hcteams.listener;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.conquest.ConquestHandler;
import rip.orbit.hcteams.events.koth.KOTH;
import rip.orbit.hcteams.team.Team;

import java.awt.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 11/07/2021 / 1:32 AM
 * HCTeams / rip.orbit.hcteams.listener
 */
public class LunarClientListener implements Listener {

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		updateWaypoints(event.getPlayer());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		updateWaypoints(event.getPlayer());
	}

	public static void updateWaypoints(Player player) {
		if (player.getWorld().getName().equals("world_nether")) {
			Team team = HCF.getInstance().getTeamHandler().getTeam("Glowstone");
			if (team != null) {
				if (team.getHq() != null) {
					LCWaypoint waypoint = new LCWaypoint("Glowstone Mountain", team.getHq(), Color.orange.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
				}
			}
			if (true) {
				LCWaypoint waypoint = new LCWaypoint("Nether Spawn", player.getWorld().getSpawnLocation(), Color.green.hashCode(), true);
				LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
			}
			Event event = HCF.getInstance().getEventHandler().getEvent("Nether");
			if (event != null) {
				if (event.isActive()) {
					LCWaypoint waypoint = new LCWaypoint("Nether KoTH", HCF.getInstance().getTeamHandler().getTeam("Nether").getHq(), Color.WHITE.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
				}
			}
		} else {
			if (player.getWorld().getName().equals("world_the_end")) {
				Team team = HCF.getInstance().getTeamHandler().getTeam("EndExit");
				if (team != null) {
					LCWaypoint waypoint = new LCWaypoint("End Exit", team.getHq(), Color.LIGHT_GRAY.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
				}
				if (true) {
					LCWaypoint waypoint = new LCWaypoint("End Spawn", player.getWorld().getSpawnLocation(), Color.RED.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
				}

				Event event = HCF.getInstance().getEventHandler().getEvent("End");
				if (event != null) {
					if (event.isActive()) {
						LCWaypoint waypoint = new LCWaypoint("End KoTH", HCF.getInstance().getTeamHandler().getTeam("End").getHq(), Color.PINK.hashCode(), true);
						LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
					}
				}
				return;
			}
			Team team = HCF.getInstance().getTeamHandler().getTeam(player);
			if (team != null) {
				if (team.getHq() != null) {
					LCWaypoint waypoint = new LCWaypoint("HQ", team.getHq(), Color.BLUE.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
				}
				if (team.getFactionFocused() != null) {
					if (team.getFactionFocused().getHq() != null) {
						LCWaypoint waypoint = new LCWaypoint(team.getFactionFocused().getName() + "'s HQ", team.getFactionFocused().getHq(), Color.orange.hashCode(), true);
						LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
					}
				}
				if (team.getRally() != null) {
					LCWaypoint waypoint = new LCWaypoint("Rally", team.getRally(), Color.yellow.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
				}
			}
			if (true) {
				LCWaypoint waypoint = new LCWaypoint("Spawn", player.getWorld().getSpawnLocation(), Color.green.hashCode(), true);
				LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
			}
			for (Event event : HCF.getInstance().getEventHandler().getEvents()) {

				if (!event.isActive()) return;
				if (event.getName().startsWith(ConquestHandler.KOTH_NAME_PREFIX)) return;

				if (event.getName().equalsIgnoreCase("Citadel")) {
					LCWaypoint waypoint = new LCWaypoint("Citadel", HCF.getInstance().getTeamHandler().getTeam("Citadel").getHq(), Color.magenta.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
				} else if (event.getName().equalsIgnoreCase("Conquest")) {

					LCWaypoint waypoint = new LCWaypoint("Conquest", player.getWorld().getSpawnLocation(), Color.orange.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
				} else {
					KOTH koth = (KOTH) event;
					Location loc = new Location(Bukkit.getWorld(koth.getWorld()), koth.getCapLocation().getX(), koth.getCapLocation().getY(), koth.getCapLocation().getZ());
					LCWaypoint waypoint = new LCWaypoint(event.getName() + " KoTH", loc, Color.cyan.hashCode(), true);
					LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
				}
			}
		}
	}

}
