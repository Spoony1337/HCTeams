package rip.orbit.hcteams.team.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import rip.orbit.hcteams.team.Team;

@AllArgsConstructor
@Getter
public class PlayerJoinTeamEvent extends Event {

	@Getter private static HandlerList handlerList = new HandlerList();

	private Player player;
	private Team team;

	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

}
