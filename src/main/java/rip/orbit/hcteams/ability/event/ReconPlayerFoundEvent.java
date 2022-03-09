package rip.orbit.hcteams.ability.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 11/07/2021 / 12:06 AM
 * HCTeams / rip.orbit.hcteams.ability.event
 */

@AllArgsConstructor
@Getter
public class ReconPlayerFoundEvent extends Event {

	@Getter
	private static HandlerList handlerList = new HandlerList();

	private Player user;
	private Player player;


	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

}
