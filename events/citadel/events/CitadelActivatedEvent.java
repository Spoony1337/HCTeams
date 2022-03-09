package rip.orbit.hcteams.events.citadel.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CitadelActivatedEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    @Override
	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}