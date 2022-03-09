package rip.orbit.hcteams.pvpclasses.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import rip.orbit.hcteams.pvpclasses.PvPClass;

@AllArgsConstructor
public class BardRestoreEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    @Getter private Player player;
    @Getter private PvPClass.SavedPotion potions;

    @Override
	public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}