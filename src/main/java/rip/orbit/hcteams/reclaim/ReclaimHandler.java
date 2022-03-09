package rip.orbit.hcteams.reclaim;

import lombok.Getter;
import org.bukkit.entity.Player;

public class ReclaimHandler {

    @Getter private ReclaimMap reclaimMap;

    public ReclaimHandler() {
        (reclaimMap = new ReclaimMap()).loadFromRedis();
    }

    public boolean hasReclaimed(Player player){
        return reclaimMap.isToggled(player.getUniqueId());
    }

    public void setUsedReclaim(Player p, boolean used){
        reclaimMap.setToggled(p.getUniqueId(), used);
    }
}
