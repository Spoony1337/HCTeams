package rip.orbit.hcteams.redeem.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.redeem.object.Partner;
import rip.orbit.hcteams.util.CC;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 9:26 PM
 * HCTeams / rip.orbit.hcteams.redeem
 */
public class PartnerAddCommand {

	@Command(names = {"partners add", "partners create"}, permission = "op")
	public static void addParty(CommandSender sender, @Param(name = "partnerName") String partnerName) {
		if (HCF.getInstance().getRedeemHandler().getPartners().contains(HCF.getInstance().getRedeemHandler().partnerByName(partnerName))) {
			sender.sendMessage(CC.translate("&cThat partner already exists."));
			return;
		}
		Partner partner = new Partner(partnerName);
		partner.save();
		HCF.getInstance().getRedeemHandler().getPartners().add(partner);

		sender.sendMessage(CC.translate("&aYou have just created a new redeemable partner name"));
	}

}
