package rip.orbit.hcteams.ability.generator.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.ability.generator.Generator;
import rip.orbit.hcteams.ability.generator.menu.button.PickUpButton;
import rip.orbit.hcteams.ability.generator.menu.button.SummaryButton;
import rip.orbit.hcteams.ability.generator.menu.button.ToggleHologramButton;
import rip.orbit.hcteams.ability.generator.menu.button.UpgradeButton;
import rip.orbit.hcteams.util.CC;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 4:39 AM
 * HCTeams / rip.orbit.hcteams.ability.generator.menu
 */

@AllArgsConstructor
public class GeneratorMenu extends Menu {

	private final Generator generator;

	@Override
	public String getTitle(Player player) {
		return CC.translate("&6Generator Settings");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(12, new SummaryButton(this.generator));
		buttons.put(14, new UpgradeButton(this.generator));
		buttons.put(19, new ToggleHologramButton(this.generator));
		buttons.put(25, new PickUpButton(this.generator));

		return buttons;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}
