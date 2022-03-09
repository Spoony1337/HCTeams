package rip.orbit.hcteams.map.killstreaks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class PersistentKillstreak {
    
    @Getter private String name;
    @Getter private int killsRequired;
    
    public boolean matchesExactly(int kills) {
        return kills == killsRequired;
    }
    
    public boolean check(int count) {
        return killsRequired <= count;
    }
    
    public void apply(Player player) {}
    
}
