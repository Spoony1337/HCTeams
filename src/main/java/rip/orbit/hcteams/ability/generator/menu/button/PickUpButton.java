package rip.orbit.hcteams.ability.generator.menu.button;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.generator.Generator;
import rip.orbit.hcteams.ability.generator.GeneratorHandler;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 4:42 AM
 * HCTeams / rip.orbit.hcteams.ability.generator.menu.button
 */

@AllArgsConstructor
public class PickUpButton extends Button {

	private final Generator generator;

	@Override
	public String getName(Player player) {
		return CC.translate("&cPick Up Generator");
	}

	@Override
	public List<String> getDescription(Player player) {
		return CC.translate(Collections.singletonList("&7Click to pick up your generator."));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.REDSTONE;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		GeneratorHandler handler = HCF.getInstance().getAbilityHandler().getGeneratorHandler();

		if (this.generator.getTier() == 1) {
			ItemUtils.tryFit(player, handler.replacedLevel(handler.getGeneratorItems().get(0), 1));
		} else if (this.generator.getTier() == 2) {
			ItemUtils.tryFit(player, handler.replacedLevel(handler.getGeneratorItems().get(1), 1));
		} else if (this.generator.getTier() == 3) {
			ItemUtils.tryFit(player, handler.replacedLevel(handler.getGeneratorItems().get(2), 1));
		}
		HCF.getInstance().getAbilityHandler().getGeneratorHandler().delete(this.generator);
		player.closeInventory();
	}
}
