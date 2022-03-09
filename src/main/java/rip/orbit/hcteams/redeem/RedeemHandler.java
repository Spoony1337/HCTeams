package rip.orbit.hcteams.redeem;

import lombok.Getter;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import org.bson.Document;
import rip.orbit.hcteams.redeem.command.param.PartnerParamType;
import rip.orbit.hcteams.redeem.map.PartnerRedeemMap;
import rip.orbit.hcteams.redeem.object.Partner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 7:10 PM
 * HCTeams / rip.orbit.hcteams.redeem
 */

@Getter
public class RedeemHandler {

	private final List<Partner> partners;
	private final PartnerRedeemMap redeemMap;

	public RedeemHandler() {
		partners = new ArrayList<>();

		for (Document document : Partner.getCollection().find()) {
			partners.add(new Partner(document.getString("name")));
		}
		FrozenCommandHandler.registerParameterType(Partner.class, new PartnerParamType());
		redeemMap = new PartnerRedeemMap();
		redeemMap.loadFromRedis();
	}

	public Partner partnerByName(String toSearch) {
		for (Partner partner : partners) {
			if (partner.getName().equals(toSearch)) {
				return partner;
			}
		}
		return null;
	}

}
