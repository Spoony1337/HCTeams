package rip.orbit.hcteams.map.killstreaks.orbittypes;

import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.map.killstreaks.PersistentKillstreak;

public class FireRes extends PersistentKillstreak {

    public FireRes() {
        super("Fire Resistance", 6);
    }

    @Override
	public void apply(Player player) {
        player.getInventory().addItem(ItemBuilder.of(Material.POTION).data((short) 8227).build());
    }
    
}
