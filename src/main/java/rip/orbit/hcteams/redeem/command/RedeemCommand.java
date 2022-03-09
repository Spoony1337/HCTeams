package rip.orbit.hcteams.redeem.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.gravity.util.uuid.UniqueIDCache;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.redeem.object.Partner;
import rip.orbit.hcteams.util.CC;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 9:40 PM
 * HCTeams / rip.orbit.hcteams.redeem
 */
public class RedeemCommand {

	@Command(names = "redeemstats", permission = "")
	public static void redeemstats(CommandSender sender) {
		sender.sendMessage(CC.translate(""));
		sender.sendMessage(CC.translate("&6&lPartner Redeem Statistics"));
		sender.sendMessage(CC.translate(""));
		for (Partner partner : HCF.getInstance().getRedeemHandler().getPartners()) {
			sender.sendMessage(CC.translate("&7┃ &f" + partner.getName() + "&7: &6" + partner.getRedeemedAmount()));
		}
		sender.sendMessage(CC.translate(""));
	}

	@Command(names = "resetredeem", permission = "op")
	public static void resetredeem(CommandSender sender, @Param(name = "player") UUID target) {
		sender.sendMessage(CC.translate("&aReset " + UniqueIDCache.name(target) + "'s Partner Redeem"));
		HCF.getInstance().getRedeemHandler().getRedeemMap().setToggled(target, false);
	}

	@Command(names = {"redeem", "claim", "claimpartner", "partners"}, permission = "")
	public static void redeem(Player sender, @Param(name = "partner") Partner partner) {
		if (partner == null) {
			sender.sendMessage(CC.translate("&cCould not find a partner with that name."));
			return;
		}
		if (HCF.getInstance().getRedeemHandler().getRedeemMap().isToggled(sender.getUniqueId())) {
			sender.sendMessage(CC.translate("&cYou have already redeemed a partner."));
			return;
		}

		sender.sendMessage(CC.translate("&aSuccessfully redeemed for " + partner.getName()));

		partner.setRedeemedAmount(partner.getRedeemedAmount() + 1);
		partner.save();

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "partnercrates give " + sender.getName() + " 2");

		HCF.getInstance().getRedeemHandler().getRedeemMap().setToggled(sender.getUniqueId(), true);
	}

}
