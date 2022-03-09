package rip.orbit.hcteams.server.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class BackstabKillEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter private Player killed;
    @Getter @Setter private boolean allowed = false;

    public BackstabKillEvent(Player who, Player killed) {
        super(who);
        this.killed = killed;
    }

    
    @Override
	public HandlerList getHandlers() {
        return handlerList;
    }

}
