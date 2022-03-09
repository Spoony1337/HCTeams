package rip.orbit.hcteams.map.killstreaks.orbittypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.map.killstreaks.Killstreak;

public class Gapple extends Killstreak {


    @Override
	public String getName() {
        return "God Apple";
    }


    @Override
	public int[] getKills() {
        return new int[] {
                21
        };
    }


    @Override
	public void apply(Player player) {
        give(player, new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1));
    }

}
