package rip.orbit.hcteams.server.task;

import rip.orbit.hcteams.HCF;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/09/2021 / 6:35 PM
 * HCTeams / rip.orbit.hcteams.server.task
 */
public class BackupRunnable implements Runnable {

	List<String> days = Arrays.asList(
			"MONDAY",
			"TUESDAY",
			"WEDNESDAY",
			"THURSDAY",
			"FRIDAY",
			"SATURDAY",
			"SUNDAY"
	);
	List<String> times = Arrays.asList(
			"00-00",
			"00-30",
			"01-00",
			"01-30",
			"02-00",
			"02-30",
			"03-00",
			"03-30",
			"04-00",
			"04-30",
			"05-00",
			"05-30",
			"06-00",
			"06-30",
			"07-00",
			"07-30",
			"08-00",
			"08-30",
			"09-00",
			"09-30",
			"10-00",
			"10-30",
			"11-00",
			"11-30",
			"12-00",
			"12-30",
			"13-00",
			"13-30",
			"14-00",
			"14-30",
			"15-00",
			"15-30",
			"16-00",
			"16-30",
			"17-00",
			"17-30",
			"18-00",
			"18-30",
			"19-00",
			"19-30",
			"20-00",
			"20-30",
			"21-00",
			"21-30",
			"22-00",
			"22-30",
			"23-00",
			"23-30");

	Calendar cal = Calendar.getInstance();

	@Override
	public void run() {
		cal = Calendar.getInstance();

		boolean isBackupDay = days.stream().anyMatch(d -> d.equalsIgnoreCase(getDayName(cal.get(Calendar.DAY_OF_WEEK))));

		if (isBackupDay) {
			for (String time : times) {
				try {
					String[] timeStr = time.split("-");

					if (timeStr[0].startsWith("0")) {
						timeStr[0] = timeStr[0].substring(1);
					}

					if (timeStr[1].startsWith("0")) {
						timeStr[1] = timeStr[1].substring(1);
					}

					int hour = Integer.valueOf(timeStr[0]);
					int minute = Integer.valueOf(timeStr[1]);

					if (cal.get(Calendar.HOUR_OF_DAY) == hour && cal.get(Calendar.MINUTE) == minute) {
						HCF.getInstance().backupTeams();
					}
				} catch (Exception e) {
					System.err.println(
							"ServerBackup: Automatic Backup failed. Please check that you set the BackupTimer correctly.");
				}
			}
		}
	}

	private String getDayName(int dayNumber) {
		if (dayNumber == 1) {
			return "SUNDAY";
		}

		if (dayNumber == 2) {
			return "MONDAY";
		}

		if (dayNumber == 3) {
			return "TUESDAY";
		}

		if (dayNumber == 4) {
			return "WEDNESDAY";
		}

		if (dayNumber == 5) {
			return "THURSDAY";
		}

		if (dayNumber == 6) {
			return "FRIDAY";
		}

		if (dayNumber == 7) {
			return "SATURDAY";
		}

		System.err.println("Error while converting number in day.");

		return null;
	}

}
