package rip.orbit.hcteams.deathmessage.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class PlayerKilledEvent extends Event {

	private static HandlerList handlerList = new HandlerList();

	@Getter private Player killer;
	@Getter private Player victim;

	@Override
	public HandlerList getHandlers() {
		return (handlerList);
	}

	public static HandlerList getHandlerList() {
		return (handlerList);
	}

}
