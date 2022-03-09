package rip.orbit.hcteams.team.teamupgrades.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.orbit.gravity.util.menu.Button;
import rip.orbit.gravity.util.menu.Menu;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.teamupgrades.enums.RegenUpgrades;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/05/2021 / 8:55 PM
 * ihcf-xenlan / me.lbuddyboy.hcf.extras.teamupgrades.menu
 */
public class DTRRegenMenu extends Menu {
	@Override
	public int size(Player player) {
		return 9;
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(3, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				return new ItemBuilder(Material.COAL)
						.displayName(CC.chat("&6DTR Regen Upgrade #1"))
						.setLore(CC.translate(Arrays.asList(
								"&7Click to upgrade your DTR Regen to 35 minutes",
								"",
								"&7Cost: &680 Meteors",
								"&7Status: " + (team.getRegenUpgrades() == RegenUpgrades.THIRTYFIVE || team.getRegenUpgrades() == RegenUpgrades.THIRTY || team.getRegenUpgrades() == RegenUpgrades.TWENTYFIVE ? "&a&lUNLOCKED" : "&c&lLOCKED")
						)))
						.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				if (team.getRegenUpgrades() == RegenUpgrades.THIRTYFIVE || team.getRegenUpgrades() == RegenUpgrades.THIRTY || team.getRegenUpgrades() == RegenUpgrades.TWENTYFIVE) {
					player.sendMessage(CC.chat("&cYou already have this upgrade unlocked."));
					return;
				}
				if (team.getMeteors() >= 80) {
					team.setMeteors(team.getMeteors() - 80);
					team.setDTRRegenUpgrades(RegenUpgrades.THIRTYFIVE.toString());
					player.sendMessage(CC.chat("&aYou have successfully bought the 35 minute DTR Regen Upgrade!"));
				} else {
					player.sendMessage(CC.chat("&cInsufficient funds."));
				}
			}
		});
		buttons.put(4, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				return new ItemBuilder(Material.IRON_INGOT)
						.displayName(CC.chat("&6DTR Regen Upgrade #2"))
						.setLore(CC.translate(Arrays.asList(
								"&7Click to upgrade your DTR Regen to 30 minutes",
								"",
								"&7Cost: &6100 Meteors",
								"&7Status: " + (team.getRegenUpgrades() == RegenUpgrades.THIRTY || team.getRegenUpgrades() == RegenUpgrades.TWENTYFIVE ? "&a&lUNLOCKED" : "&c&lLOCKED")
						)))
						.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				if (team.getRegenUpgrades() == RegenUpgrades.THIRTY || team.getRegenUpgrades() == RegenUpgrades.TWENTYFIVE) {
					player.sendMessage(CC.chat("&cYou already have this upgrade unlocked."));
					return;
				}
				if (team.getRegenUpgrades() != RegenUpgrades.THIRTYFIVE) {
					player.sendMessage(CC.chat("&cYou must purchase the thirty-five minute upgrade before you purchase this one."));
					return;
				}
				if (team.getMeteors() >= 100) {
					team.setMeteors(team.getMeteors() - 100);
					team.setDTRRegenUpgrades(RegenUpgrades.THIRTY.toString());
					player.sendMessage(CC.chat("&aYou have successfully bought the 30 minute DTR Regen Upgrade!"));
				} else {
					player.sendMessage(CC.chat("&cInsufficient funds."));
				}
			}
		});
		buttons.put(5, new Button() {
			@Override
			public ItemStack getButtonItem(Player player) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				return new ItemBuilder(Material.EMERALD)
						.displayName(CC.chat("&6DTR Regen Upgrade #3"))
						.setLore(CC.translate(Arrays.asList(
								"&7Click to upgrade your DTR Regen to 30 minutes",
								"",
								"&7Cost: &6150 Meteors",
								"&7Status: " + (team.getRegenUpgrades() == RegenUpgrades.TWENTYFIVE ? "&a&lUNLOCKED" : "&c&lLOCKED")
						)))
						.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				if (team.getRegenUpgrades() == RegenUpgrades.TWENTYFIVE) {
					player.sendMessage(CC.chat("&cYou already have this upgrade unlocked."));
					return;
				}
				if (team.getRegenUpgrades() != RegenUpgrades.THIRTY) {
					player.sendMessage(CC.chat("&cYou must purchase the thirty five minute upgrade before you purchase this one."));
					return;
				}
				if (team.getMeteors() >= 150) {
					team.setMeteors(team.getMeteors() - 150);
					team.setDTRRegenUpgrades(RegenUpgrades.TWENTYFIVE.toString());
					player.sendMessage(CC.chat("&aYou have successfully bought the 25 minute DTR Regen Upgrade!"));
				} else {
					player.sendMessage(CC.chat("&cInsufficient funds."));
				}
			}
		});
		return buttons;
	}

	@Override
	public String getTitle(Player player) {
		return "DTR Upgrades";
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}
