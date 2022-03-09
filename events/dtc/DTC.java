package rip.orbit.hcteams.events.dtc;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.events.Event;
import rip.orbit.hcteams.events.EventType;
import rip.orbit.hcteams.events.events.EventActivatedEvent;
import rip.orbit.hcteams.events.events.EventCapturedEvent;
import rip.orbit.hcteams.events.events.EventDeactivatedEvent;

import java.util.concurrent.TimeUnit;

public class DTC implements Event {

    @Getter private String name;
    @Getter private BlockVector capLocation;
    @Getter private String world;
    @Getter private boolean hidden = false;
    @Getter @Setter boolean active;

    @Getter private int currentPoints = 0;
    @Getter private int startingPoints = 175;

    @Getter private long lastBlockBreak = -1L;
    @Getter private long lastPointIncrease = -1L;

    @Getter private EventType type = EventType.DTC;

    public DTC(String name, Location location) {
        this.name = name;
        this.capLocation = location.toVector().toBlockVector();
        this.world = location.getWorld().getName();
        this.currentPoints = this.startingPoints;

        HCF.getInstance().getEventHandler().getEvents().add(this);
        HCF.getInstance().getEventHandler().saveEvents();
    }

    public void setLocation(Location location) {
        this.capLocation = location.toVector().toBlockVector();
        this.world = location.getWorld().getName();
        HCF.getInstance().getEventHandler().saveEvents();
    }

    
    @Override
	public void tick() {
        if (this.currentPoints == this.startingPoints) {
            return;
        } else if (this.startingPoints <= this.currentPoints) {
            this.currentPoints = this.startingPoints;
        }

        long timeSinceLastBlockBreak = System.currentTimeMillis() - lastBlockBreak;
        long timeSinceLastPointIncrease = System.currentTimeMillis() - lastPointIncrease;

        if (TimeUnit.SECONDS.toMillis(15) <= timeSinceLastBlockBreak && TimeUnit.SECONDS.toMillis(15) <= timeSinceLastPointIncrease) {
            this.currentPoints++;
            this.lastPointIncrease = System.currentTimeMillis();

            if (this.currentPoints % 10 == 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[DTC] &e" + this.getName() + " &6is regenerating &9[" + this.getCurrentPoints() + "]"));
            }
        }
    }

    
    @Override
	public void setHidden(boolean hidden) {
        this.hidden = hidden;
        HCF.getInstance().getEventHandler().saveEvents();
    }

    @Override
	public boolean activate() {
        if (active) {
            return (false);
        }

        HCF.getInstance().getServer().getPluginManager().callEvent(new EventActivatedEvent(this));

        this.active = true;
        this.currentPoints = startingPoints;

        return (true);
    }

    @Override
	public boolean deactivate() {
        if (!active) {
            return (false);
        }

        HCF.getInstance().getServer().getPluginManager().callEvent(new EventDeactivatedEvent(this));

        this.active = false;
        this.currentPoints = startingPoints;

        return (true);
    }

    public void blockBroken(Player player) {
        if (--this.currentPoints <= 0) {
            HCF.getInstance().getServer().getPluginManager().callEvent(new EventCapturedEvent(this, player));
            deactivate();
        }

        if (this.currentPoints % 10 == 0) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[DTC] &e" + this.getName() + " &6is being broken &9[" + this.getCurrentPoints() + "]"));
        }

        this.lastBlockBreak = System.currentTimeMillis();
    }
    
}
