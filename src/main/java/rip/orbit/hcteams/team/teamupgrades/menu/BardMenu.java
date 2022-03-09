package rip.orbit.hcteams.team.teamupgrades.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.orbit.gravity.util.menu.Button;
import rip.orbit.gravity.util.menu.Menu;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.teamupgrades.enums.BardUpgrades;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 31/05/2021 / 10:44 PM
 * ihcf-xenlan / me.lbuddyboy.hcf.extras.teamupgrades.menu
 */
public class BardMenu extends Menu {
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
						.displayName(CC.chat("&6Bard Buff Upgrade #1"))
						.setLore(CC.translate(Arrays.asList(
								"&7Click to upgrade your Bard Buff Cooldown to 8 seconds",
								"",
								"&7Cost: &650 Meteors",
								"&7Status: " + (team.getBardUpgrades() == BardUpgrades.EIGHTBUFFDELAY || team.getBardUpgrades() == BardUpgrades.SIXBUFFDELAY || team.getBardUpgrades() == BardUpgrades.FOURBUFFDELAY ? "&a&lUNLOCKED" : "&c&lLOCKED")
						)))
						.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				if (team.getBardUpgrades() == BardUpgrades.EIGHTBUFFDELAY || team.getBardUpgrades() == BardUpgrades.FOURBUFFDELAY|| team.getBardUpgrades() == BardUpgrades.SIXBUFFDELAY) {
					player.sendMessage(CC.chat("&cYou already have this upgrade unlocked."));
					return;
				}
				if (team.getMeteors() >= 50) {
					team.setMeteors(team.getMeteors() - 50);
					team.setBardUpgrades(BardUpgrades.EIGHTBUFFDELAY.toString());
					player.sendMessage(CC.chat("&aYou have successfully bought the 8 second bard buff cooldown!"));
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
						.displayName(CC.chat("&6Bard Buff Upgrade #2"))
						.setLore(CC.translate(Arrays.asList(
								"&7Click to upgrade your Bard Buff Cooldown to 6 seconds",
								"",
								"&7Cost: &6100 Meteors",
								"&7Status: " + (team.getBardUpgrades() == BardUpgrades.SIXBUFFDELAY || team.getBardUpgrades() == BardUpgrades.FOURBUFFDELAY ? "&a&lUNLOCKED" : "&c&lLOCKED")
						)))
						.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				if (team.getBardUpgrades() == BardUpgrades.FOURBUFFDELAY|| team.getBardUpgrades() == BardUpgrades.SIXBUFFDELAY) {
					player.sendMessage(CC.chat("&cYou already have this upgrade unlocked."));
					return;
				}
				if (team.getBardUpgrades() != BardUpgrades.EIGHTBUFFDELAY) {
					player.sendMessage(CC.chat("&cYou must purchase the 8 second buff delay upgrade before you purchase this one."));
					return;
				}
				if (team.getMeteors() >= 100) {
					team.setMeteors(team.getMeteors() - 100);
					team.setBardUpgrades(BardUpgrades.SIXBUFFDELAY.toString());
					player.sendMessage(CC.chat("&aYou have successfully bought the 6 second bard buff cooldown!"));
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
						.displayName(CC.chat("&6Bard Buff Upgrade #3"))
						.setLore(CC.translate(Arrays.asList(
								"&7Click to upgrade your Bard Buff Cooldown to 4 seconds",
								"",
								"&7Cost: &6125 Meteors",
								"&7Status: " + (team.getBardUpgrades() == BardUpgrades.FOURBUFFDELAY ? "&a&lUNLOCKED" : "&c&lLOCKED")
						)))
						.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				if (team.getBardUpgrades() == BardUpgrades.FOURBUFFDELAY) {
					player.sendMessage(CC.chat("&cYou already have this upgrade unlocked."));
					return;
				}
				if (team.getBardUpgrades() != BardUpgrades.SIXBUFFDELAY) {
					player.sendMessage(CC.chat("&cYou must purchase the 6 second buff delay upgrade before you purchase this one."));
					return;
				}
				if (team.getMeteors() >= 125) {
					team.setMeteors(team.getMeteors() - 125);
					team.setBardUpgrades(BardUpgrades.FOURBUFFDELAY.toString());
					player.sendMessage(CC.chat("&aYou have successfully bought the 4 second bard buff cooldown!"));
				} else {
					player.sendMessage(CC.chat("&cInsufficient funds."));
				}
			}
		});
		return buttons;
	}

	@Override
	public String getTitle(Player player) {
		return "Bard Upgrades";
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
