package rip.orbit.hcteams.team.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import rip.orbit.hcteams.team.Team;

public class PlayerBuildInOthersClaimEvent extends PlayerEvent {

    @Getter private static HandlerList handlerList = new HandlerList();

    @Getter @Setter private boolean willIgnore;
    @Getter private Block block;
    @Getter private Team team;

    public PlayerBuildInOthersClaimEvent(Player who, Block block, Team team) {
        super(who);
        this.block = block;
        this.team = team;
    }

    
    @Override
	public HandlerList getHandlers() {
        return handlerList;
    }

}
