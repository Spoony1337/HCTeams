package rip.orbit.hcteams.ability.generator.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.pagination.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.gravity.profile.punishment.Punishment;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.generator.Generator;
import rip.orbit.hcteams.ability.generator.GeneratorHandler;
import rip.orbit.hcteams.util.CC;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 4:52 AM
 * HCTeams / rip.orbit.hcteams.ability.generator.menu
 */
public class GeneratorsMenu extends PaginatedMenu {

	@Override
	public String getPrePaginatedTitle(Player player) {
		return CC.translate("&6Your Generators");
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		GeneratorHandler handler = HCF.getInstance().getAbilityHandler().getGeneratorHandler();

		int i = 0;
		for (Generator generator : handler.getGenerators()) {
			if (generator.getOwner().equals(player.getUniqueId())) {
				buttons.put(i, new GeneratorButton(generator));
				++i;
			}
		}

		return buttons;
	}

	@AllArgsConstructor
	public static class GeneratorButton extends Button {

		private final Generator generator;

		@Override
		public String getName(Player player) {
			return CC.translate(generator.displayName());
		}

		@Override
		public List<String> getDescription(Player player) {

			int x = (int) generator.getX();
			int y = (int) generator.getY();
			int z = (int) generator.getZ();

			return CC.translate(Arrays.asList(
					"",
					"&fTier&7: " + generator.displayColor() + generator.getTier(),
					"&fLevel&7: " + generator.displayColor() + generator.getLevel(),
					"&fSpawn Time&7: " + generator.displayColor() + Punishment.TimeUtils.formatIntoMMSS(generator.getRemaining()),
					"&fTime Per Ability&7: " + generator.displayColor() + (generator.getLevel() + generator.getTier()) + " Ability Items Every 15 minutes",
					"",
					"&fLocation&7: " + generator.displayColor() + x + ", " + y + ", " + z,
					""
			));
		}

		@Override
		public Material getMaterial(Player player) {
			return generator.displayMaterial();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {

			if (player.isOp()) {
				generator.spawnAbilities();
				player.sendMessage(CC.translate("&eForcefully spawned abilities for the generator."));
			}

		}
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}
