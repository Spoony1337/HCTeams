package rip.orbit.hcteams.customtimer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 01/07/2021 / 11:51 PM
 * HCTeams / rip.orbit.hcteams.customtimer
 */

@AllArgsConstructor
@Getter
@Setter
public class CustomTimer {

	public static List<CustomTimer> customTimers = new ArrayList<>();

	private String name;
	private String command;
	private long time;

	public static CustomTimer byName(String name) {
		for (CustomTimer timer : customTimers) {
			if (timer.getName().equalsIgnoreCase(name)) {
				return timer;
			}
		}
		return null;
	}

}
