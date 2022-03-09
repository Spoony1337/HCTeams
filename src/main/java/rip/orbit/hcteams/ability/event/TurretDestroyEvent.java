package rip.orbit.hcteams.ability.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 02/07/2021 / 1:45 AM
 * HCTeams / rip.orbit.hcteams.ability.event
 */
public class TurretDestroyEvent extends Event {

	private static HandlerList handlers = new HandlerList();

	@Getter
	private Player destroyer;

	public TurretDestroyEvent(Player destroyer) {
		this.destroyer = destroyer;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
