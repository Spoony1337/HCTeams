package rip.orbit.hcteams.redeem.command.param;

import com.google.common.collect.Lists;
import net.frozenorb.qlib.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.redeem.object.Partner;

import java.util.List;
import java.util.Set;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 9:41 PM
 * HCTeams / rip.orbit.hcteams.redeem
 */
public class PartnerParamType implements ParameterType<Partner> {
	@Override
	public Partner transform(CommandSender sender, String source) {
		return HCF.getInstance().getRedeemHandler().partnerByName(source);
	}

	@Override
	public List<String> tabComplete(Player player, Set<String> flags, String source) {
		List<String> completions = Lists.newArrayList();

		for (Partner partner : HCF.getInstance().getRedeemHandler().getPartners()) {
			if (StringUtils.startsWithIgnoreCase(partner.getName(), source)) {
				completions.add(partner.getName());
			}
		}

		return completions;
	}
}
