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
import rip.orbit.hcteams.team.teamupgrades.enums.MeteorUpgrades;
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
public class MeteorMenu extends Menu {
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
						.displayName(CC.chat("&6Meteor Upgrade #1"))
						.setLore(CC.translate(Arrays.asList(
								"&7Click to upgrade your Meteor's Earned to +2 everytime you earn them.",
								"",
								"&7Cost: &665 Meteors",
								"&7Status: " + (team.getMeteorUpgrades() == MeteorUpgrades.x2 || team.getMeteorUpgrades() == MeteorUpgrades.x4 || team.getMeteorUpgrades() == MeteorUpgrades.x6 ? "&a&lUNLOCKED" : "&c&lLOCKED")
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
				if (team.getMeteors() >= 65) {
					team.setMeteors(team.getMeteors() - 65);
					team.setMeteorUpgrades(MeteorUpgrades.x2.toString());
					player.sendMessage(CC.chat("&aYou have successfully bought the +2 meteor's earned!"));
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
						.displayName(CC.chat("&6Meteor Upgrade #2"))
						.setLore(CC.translate(Arrays.asList(
								"&7Click to upgrade your Meteor's Earned to +4 everytime you earn them.",
								"",
								"&7Cost: &6100 Meteors",
								"&7Status: " + (team.getMeteorUpgrades() == MeteorUpgrades.x4 || team.getMeteorUpgrades() == MeteorUpgrades.x6 ? "&a&lUNLOCKED" : "&c&lLOCKED")
						)))
						.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				if (team.getMeteorUpgrades() == MeteorUpgrades.x6|| team.getMeteorUpgrades() == MeteorUpgrades.x4) {
					player.sendMessage(CC.chat("&cYou already have this upgrade unlocked."));
					return;
				}
				if (team.getMeteorUpgrades() != MeteorUpgrades.x2) {
					player.sendMessage(CC.chat("&cYou must purchase the +2 meteor upgrade before you purchase this one."));
					return;
				}
				if (team.getMeteors() >= 100) {
					team.setMeteors(team.getMeteors() - 100);
					team.setMeteorUpgrades(MeteorUpgrades.x4.toString());
					player.sendMessage(CC.chat("&aYou have successfully bought the +4 meteor upgrade!"));
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
						.displayName(CC.chat("&6Meteor Upgrade #3"))
						.setLore(CC.translate(Arrays.asList(
								"&7Click to upgrade your Meteor's Earned to +6 everytime you earn them.",
								"",
								"&7Cost: &6150 Meteors",
								"&7Status: " + (team.getMeteorUpgrades() == MeteorUpgrades.x6 ? "&a&lUNLOCKED" : "&c&lLOCKED")
						)))
						.build();
			}

			@Override
			public void clicked(Player player, ClickType clickType) {
				Team team = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
				if (team.getMeteorUpgrades() == MeteorUpgrades.x6) {
					player.sendMessage(CC.chat("&cYou already have this upgrade unlocked."));
					return;
				}
				if (team.getMeteorUpgrades() != MeteorUpgrades.x4) {
					player.sendMessage(CC.chat("&cYou must purchase the +4 meteor upgrade before you purchase this one."));
					return;
				}
				if (team.getMeteors() >= 150) {
					team.setMeteors(team.getMeteors() - 150);
					team.setMeteorUpgrades(MeteorUpgrades.x6.toString());
					player.sendMessage(CC.chat("&aYou have successfully bought the +6 meteor upgrade!"));
				} else {
					player.sendMessage(CC.chat("&cInsufficient funds."));
				}
			}
		});
		return buttons;
	}

	@Override
	public String getTitle(Player player) {
		return "Meteor Upgrades";
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
