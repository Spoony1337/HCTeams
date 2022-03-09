package rip.orbit.hcteams.team.teamupgrades.button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.orbit.gravity.util.menu.Button;
import rip.orbit.hcteams.team.teamupgrades.menu.MeteorMenu;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemBuilder;

import java.util.Arrays;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/05/2021 / 8:52 PM
 * ihcf-xenlan / me.lbuddyboy.hcf.extras.teamupgrades.button
 */
public class TokenUpgradesButton extends Button {
	@Override
	public ItemStack getButtonItem(Player player) {
		return new ItemBuilder(Material.PAPER).displayName(CC.chat("&a&lMeteor Upgrades"))
				.setLore(CC.translate(Arrays.asList(
						"&7Click to view all of the Meteor upgrades."
						)))
				.build();
	}

	@Override
	public void clicked(Player player, ClickType clickType) {
		player.closeInventory();
		new MeteorMenu().openMenu(player);
	}
}
