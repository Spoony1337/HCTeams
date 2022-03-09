package rip.orbit.hcteams.ability.generator.menu.button;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.ability.generator.Generator;
import rip.orbit.hcteams.util.CC;

import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 4:42 AM
 * HCTeams / rip.orbit.hcteams.ability.generator.menu.button
 */

@AllArgsConstructor
public class ToggleHologramButton extends Button {

	private final Generator generator;

	@Override
	public String getName(Player player) {
		return CC.translate("&eToggle Hologram");
	}

	@Override
	public List<String> getDescription(Player player) {
		return CC.translate(Collections.singletonList("&7Click to toggle on/off the hologram above your generator."));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.SIGN;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		if (this.generator.getHologram() == null) {
			this.generator.createHologram();
		} else {
			this.generator.getHologram().clearLines();
			this.generator.setHologram(null);
		}
	}
}
