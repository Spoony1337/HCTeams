package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.CC;

public class FFACommand {

    public static boolean mode;
    private static BukkitTask runnable;

    static {
        mode = false;
    }

    @Command(names={ "FFA" }, permission="foxtrot.FFA")
    public static void ffa(Player sender) {if (!HCF.getInstance().getConfig().getBoolean("FFA-COMMAND")) {
        sender.sendMessage(CC.translate("&cThis command is only executable in Boolean Active. &7edit boolean in config.yml"));
        return;
    }

        if(mode == false) {
            mode = true;
            runnable = new BukkitRunnable() {


                @Override
				public void run() {
                    for(Player players : Bukkit.getOnlinePlayers()) {
                        if(!players.hasPermission("rank.staff")) {
                            players.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                            players.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
                            players.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                        }
                    }
                }
            }.runTaskTimerAsynchronously(HCF.getInstance(), 0L, 600L);
            sender.sendMessage(CC.translate("&aFFA Mode activated!"));
        } else {
            mode = false;
            sender.sendMessage(CC.translate("&cFFA Mode disabled!"));
            runnable.cancel();
            for(Player players : Bukkit.getOnlinePlayers()) {
                if(!players.hasPermission("rank.staff")) {
                    players.removePotionEffect(PotionEffectType.SPEED);
                    players.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                    players.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
        }
        return;
    }

}
