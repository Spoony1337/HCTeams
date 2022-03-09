package rip.orbit.hcteams.stars.menu.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import rip.orbit.hcteams.util.CC;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 3:18 PM
 * HCTeams / rip.orbit.hcteams.stars.menu.type
 */

@AllArgsConstructor
@Getter
public enum StarShopItem {

	GENERATOR3(6000, 1, Material.GOLD_BLOCK, 0, CC.translate("&e&lTier 3 Generator"), true, "generator give %player% 3 1"),
	GENERATOR2(4500, 1, Material.LAPIS_BLOCK, 0, CC.translate("&9&lTier 2 Generator"), true, "generator give %player% 2 1"),
	GENERATOR1(1500, 1, Material.COAL_BLOCK, 0, CC.translate("&8&lTier 1 Generator"), true, "generator give %player% 1 1"),
	NINJASTAR(1000, 1, Material.NETHER_STAR, 0, CC.translate("&a&lNinjaStar"), true, "ability give %player% ninjastar 1"),
	ABILITYMASTER(750, 1, Material.DIAMOND, 0, CC.translate("&6Ability Master I"), true, "gkits giveabilitymaster %player%"),
	DOME(200, 2, Material.STAINED_GLASS, 0, CC.translate("&3&lDome"), true, "ability give %player% dome 2"),
	WARRIOR(175, 3, Material.INK_SACK, 1, CC.translate("&c&lWarrior Ability"), true, "ability give %player% warrior 3"),
	GUARDIANANGEL(150, 3, Material.WATCH, 0, CC.translate("&5&lGuardian Angel"), true, "ability give %player% guardianangel 3"),
	THORNS(120, 4, Material.QUARTZ, 0, CC.translate("&e&lThorns Ability"), true, "ability give %player% thorns 4"),
	TIMEWARP(90, 4, Material.FEATHER, 0, CC.translate("&6&lTime Warp"), true, "ability give %player% timewarp 4"),
	GHOSTMODE(90, 3, Material.QUARTZ, 0, CC.translate("&8&lGhost Mode"), true, "ability give %player% ghostmode 3"),
	CURSE(75, 2, Material.ENCHANTED_BOOK, 0, CC.translate("&2&lInventory Curse"), true, "ability give %player% inventorycurse 2"),
	POCKETBARD(75, 3, Material.DOUBLE_PLANT, 0, CC.translate("&6&lPocketBard"), true, "ability give %player% pocketbard 3"),
	ANTIBUILDSTICK(75, 3, Material.STICK, 0, CC.translate("&6&lAntiBuildStick"), true, "ability give %player% antibuildstick 3"),
	SWITCHER(75, 4, Material.SNOW_BALL, 0, CC.translate("&a&lSwitcher"), true, "ability give %player% switcher 4"),
	RECON(60, 2, Material.COMPASS, 0, CC.translate("&d&lRecon Ability"), true, "ability give %player% recon 2"),
	SPEEDII(60, 1, Material.DIAMOND, 0, CC.translate("&6Speed II"), true, "givegem %player% Speed II"),
	FRES(55, 1, Material.DIAMOND, 0, CC.translate("&6Inferno I"), true, "givegem %player% Inferno I"),
	REPAIR(55, 1, Material.DIAMOND, 0, CC.translate("&6Repair I"), true, "givegem %player% Repair I"),
	RECOVER(50, 1, Material.DIAMOND, 0, CC.translate("&6Recover I"), true, "givegem %player% Recover I"),
	REPLENISH(45, 1, Material.DIAMOND, 0, CC.translate("&6Replenish I"), true, "givegem %player% Replenish I"),
	ABILITYINSPECTOR(45, 2, Material.BOOK, 0, CC.translate("&5&lAbility Inspector"), true, "ability give %player% abilityinspector 2");

	private final int price, amount;
	private final Material material;
	private final int data;
	private final String displayName;
	private final boolean useCommand;
	private final String command;

}
