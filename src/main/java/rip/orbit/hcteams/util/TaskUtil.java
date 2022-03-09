package rip.orbit.hcteams.util;

import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;

public class TaskUtil {

    public static void run(Runnable runnable) {
        HCF.getInstance().getServer().getScheduler().runTask(HCF.getInstance(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        HCF.getInstance().getServer().getScheduler().runTaskTimer(HCF.getInstance(), runnable, delay, timer);
    }

    public static void runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(HCF.getInstance(), delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        HCF.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(HCF.getInstance(), runnable, delay);
    }

    public static void runAsync(Runnable runnable) {
        HCF.getInstance().getServer().getScheduler().runTaskAsynchronously(HCF.getInstance(), runnable);
    }

}
