package rip.orbit.hcteams.ability.generator.menu.button;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.gravity.profile.punishment.Punishment;
import rip.orbit.hcteams.ability.generator.Generator;
import rip.orbit.hcteams.util.CC;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 4:42 AM
 * HCTeams / rip.orbit.hcteams.ability.generator.menu.button
 */

@AllArgsConstructor
public class SummaryButton extends Button {

	private final Generator generator;

	@Override
	public String getName(Player player) {
		return CC.translate("&eGenerator Information");
	}

	@Override
	public List<String> getDescription(Player player) {
		return CC.translate(Arrays.asList(
				" ",
				"&eStatistics",
				"&7┃ &fTier&7: &e" + this.generator.getTier(),
				"&7┃ &fLevel&7: &e" + this.generator.getLevel(),
				"&7┃ &fSpawn Time&7: &e" + Punishment.TimeUtils.formatIntoMMSS(generator.getRemaining()),
				"&7┃ &fDelay Per Ability&7: &e" + this.generator.getDelay() + " Minutes",
				" ",
				"&eInformation",
				"&7┃ &fUpgrade the levels of this",
				"&7┃ &fby using our second currency",
				"&7┃ &f'stars'.",
				"",
				"&7&oTIP: 1 Level = +1 Ability Per Generation",
				" "
		));
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.PAINTING;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
	}
}
