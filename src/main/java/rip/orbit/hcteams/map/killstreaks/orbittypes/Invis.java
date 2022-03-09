package rip.orbit.hcteams.map.killstreaks.orbittypes;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import rip.orbit.hcteams.map.killstreaks.PersistentKillstreak;

public class Invis extends PersistentKillstreak {

    public Invis() {
        super("Invis", 27);
    }
    
    @Override
	public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600*20, 1));
    }
    
}
