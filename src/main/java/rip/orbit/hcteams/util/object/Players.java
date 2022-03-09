package rip.orbit.hcteams.util.object;

import org.bukkit.entity.Player;

public class Players {

	public static boolean isNaked(Player player) {
		return player.getInventory().getHelmet() == null && player.getInventory().getChestplate() == null && player.getInventory().getLeggings() == null && player.getInventory().getBoots() == null;
	}

}
