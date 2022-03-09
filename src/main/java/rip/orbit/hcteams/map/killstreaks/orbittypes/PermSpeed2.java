package rip.orbit.hcteams.map.killstreaks.orbittypes;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.map.killstreaks.PersistentKillstreak;

public class PermSpeed2 extends PersistentKillstreak {

    public PermSpeed2() {
        super("Permanent Speed 2", 30);
    }
    
    @Override
	public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
    
}
