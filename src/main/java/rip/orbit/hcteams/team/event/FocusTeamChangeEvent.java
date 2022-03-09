package rip.orbit.hcteams.team.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import rip.orbit.hcteams.team.Team;

@RequiredArgsConstructor
@Getter
public class FocusTeamChangeEvent extends Event {

	@Getter private static HandlerList handlerList = new HandlerList();

	private final Team oldTeam;
	private final Team team;
	private final Team newTeam;


	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

}