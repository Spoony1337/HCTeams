package rip.orbit.hcteams.events.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class EventDeactivatedEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    @Getter private rip.orbit.hcteams.events.Event event;

    @Override
	public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}