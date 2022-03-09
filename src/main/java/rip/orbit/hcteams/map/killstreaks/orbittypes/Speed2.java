package rip.orbit.hcteams.map.killstreaks.orbittypes;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.map.killstreaks.PersistentKillstreak;

public class Speed2 extends PersistentKillstreak {

    public Speed2() {
        super("Speed 2", 12);
    }
    
    @Override
	public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300*20, 1));
    }
    
}
