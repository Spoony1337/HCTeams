package rip.orbit.hcteams.ability.generator.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.ability.generator.GeneratorHandler;
import rip.orbit.hcteams.ability.generator.menu.GeneratorsMenu;
import rip.orbit.hcteams.util.CC;
import rip.orbit.hcteams.util.item.ItemUtils;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/08/2021 / 4:28 AM
 * HCTeams / rip.orbit.hcteams.ability.generator.command
 */
public class GeneratorCommand {

	@Command(names = {"generators"}, permission = "")
	public static void generators(Player sender) {
		new GeneratorsMenu().openMenu(sender);
	}

	@Command(names = {"generator give"}, permission = "op")
	public static void giveGenerator(CommandSender sender, @Param(name = "target") Player target, @Param(name = "tier") int tier, @Param(name = "level") int level) {
		GeneratorHandler handler = HCF.getInstance().getAbilityHandler().getGeneratorHandler();

		ItemStack stack;
		if (tier == 1) {
			stack = handler.replacedLevel(handler.getGeneratorItems().get(0).clone(), level);
		} else if (tier == 2) {
			stack = handler.replacedLevel(handler.getGeneratorItems().get(1).clone(), level);
		} else if (tier == 3) {
			stack = handler.replacedLevel(handler.getGeneratorItems().get(2).clone(), level);
		} else {
			sender.sendMessage(CC.translate("&cThere's no generator with that tier"));
			return;
		}

		ItemUtils.tryFit(target, stack);

	}

}
