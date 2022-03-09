package rip.orbit.hcteams.events.citadel.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;

public class CitadelSaveTask extends BukkitRunnable {

    @Override
    public void run() {
        HCF.getInstance().getCitadelHandler().saveCitadelInfo();
    }

}