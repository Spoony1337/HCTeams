package rip.orbit.hcteams.ability.generator.menu.button;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.generator.Generator;
import rip.orbit.hcteams.ability.generator.GeneratorHandler;
import rip.orbit.hcteams.persist.maps.StarsMap;
import rip.orbit.hcteams.util.CC;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 4:42 AM
 * HCTeams / rip.orbit.hcteams.ability.generator.menu.button
 */

@AllArgsConstructor
public class UpgradeButton extends Button {

	private final Generator generator;

	@Override
	public String getName(Player player) {
		return CC.translate("&6Upgrade Generator");
	}

	@Override
	public List<String> getDescription(Player player) {
		return CC.translate(Arrays.asList(
				"&7┃ &fClick to upgrade your generator.",
				"&7┃ &fCost&7: &6" + this.generator.nextPrice()
		));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.NETHER_STAR;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		GeneratorHandler handler = HCF.getInstance().getAbilityHandler().getGeneratorHandler();
		StarsMap starsMap = HCF.getInstance().getStarsMaps();
		int stars = starsMap.get(player.getUniqueId());

		if (this.generator.getLevel() == 5) {
			player.sendMessage(CC.translate("&cYou are at the max level."));
			return;
		}

		if (stars < this.generator.nextPrice()) {
			player.sendMessage(CC.translate("&cInsufficient funds."));
			return;
		}

		starsMap.set(player.getUniqueId(), stars - this.generator.nextPrice());

		int previous = this.generator.getLevel();

		this.generator.setLevel(this.generator.getLevel() + 1);
		this.generator.setRemaining(60 * this.generator.getDelay());
		this.generator.save();

		player.sendMessage(CC.translate("&aYou have just upgraded your generator from level " + previous + " to " + (previous + 1)));
	}

}
