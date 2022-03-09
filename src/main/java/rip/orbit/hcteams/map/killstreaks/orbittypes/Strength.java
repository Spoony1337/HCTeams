package rip.orbit.hcteams.map.killstreaks.orbittypes;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.map.killstreaks.PersistentKillstreak;

public class Strength extends PersistentKillstreak {

    public Strength() {
        super("Strength", 18);
    }
    
    @Override
	public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 45*20, 1));
    }
    
}