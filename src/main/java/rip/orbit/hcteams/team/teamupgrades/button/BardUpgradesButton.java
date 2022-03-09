package rip.orbit.hcteams.team.teamupgrades.button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.orbit.gravity.util.menu.Button;
import rip.orbit.hcteams.team.teamupgrades.menu.BardMenu;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemBuilder;

import java.util.Arrays;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/05/2021 / 8:52 PM
 * ihcf-xenlan / me.lbuddyboy.hcf.extras.teamupgrades.button
 */
public class BardUpgradesButton extends Button {
	@Override
	public ItemStack getButtonItem(Player player) {
		return new ItemBuilder(Material.GOLD_CHESTPLATE).displayName(CC.chat("&a&lBard Buff Cooldown Upgrades"))
				.setLore(CC.translate(Arrays.asList(
						"&7Click to view all of the Bard Buff Cooldown upgrades."
						)))
				.build();
	}

	@Override
	public void clicked(Player player, ClickType clickType) {
		player.closeInventory();
		new BardMenu().openMenu(player);
	}
}
