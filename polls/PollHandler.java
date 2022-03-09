package rip.orbit.hcteams.polls;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 5:56 PM
 * HCTeams / rip.orbit.hcteams.polls
 */
public class PollHandler {

	@Getter private final List<Poll> polls;

	public PollHandler() {
		polls = new ArrayList<>();
	}

}
