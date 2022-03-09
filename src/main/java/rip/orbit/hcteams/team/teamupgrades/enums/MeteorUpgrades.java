package rip.orbit.hcteams.team.teamupgrades.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MeteorUpgrades {

	DEFAULT(0), x2(2), x4(4), x6(6);

	@Getter private int addition;

}
