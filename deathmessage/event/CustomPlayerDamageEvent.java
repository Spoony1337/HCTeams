package rip.orbit.hcteams.deathmessage.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import rip.orbit.hcteams.deathmessage.objects.Damage;

@AllArgsConstructor
public class CustomPlayerDamageEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    @Getter private EntityDamageEvent cause;
    @Getter @Setter private Damage trackerDamage;

    public Player getPlayer() {
        return ((Player) cause.getEntity());
    }

    public double getDamage() {
        return (cause.getDamage());
    }

    @Override
	public HandlerList getHandlers() {
        return (handlerList);
    }

    public static HandlerList getHandlerList() {
        return (handlerList);
    }

}