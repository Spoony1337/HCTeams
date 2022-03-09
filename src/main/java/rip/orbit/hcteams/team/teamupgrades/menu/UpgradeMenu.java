package rip.orbit.hcteams.team.teamupgrades.menu;

import org.bukkit.entity.Player;
import rip.orbit.gravity.util.menu.Button;
import rip.orbit.gravity.util.menu.Menu;
import rip.orbit.hcteams.team.teamupgrades.button.BardUpgradesButton;
import rip.orbit.hcteams.team.teamupgrades.button.DTRRegenButton;
import rip.orbit.hcteams.team.teamupgrades.button.TokenUpgradesButton;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/05/2021 / 8:52 PM
 * ihcf-xenlan / me.lbuddyboy.hcf.extras.teamupgrades.menu
 */
public class UpgradeMenu extends Menu {

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(11, new DTRRegenButton());
		buttons.put(13, new BardUpgradesButton());
		buttons.put(15, new TokenUpgradesButton());

		return buttons;
	}

	@Override
	public int size(Player player) {
		return 27;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@Override
	public String getTitle(Player player) {
		return "Team Upgrades";
	}
}
