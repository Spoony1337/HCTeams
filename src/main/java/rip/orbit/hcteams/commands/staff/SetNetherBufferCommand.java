package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;

public class SetNetherBufferCommand {

    @Command(names={ "SetNetherBuffer" }, permission="op")
    public static void setNetherBuffer(Player sender, @Param(name="netherBuffer") int newBuffer) {
        HCF.getInstance().getMapHandler().setNetherBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The nether buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            
            @Override
			public void run() {
                HCF.getInstance().getMapHandler().saveNetherBuffer();
            }

        }.runTaskAsynchronously(HCF.getInstance());
    }

}
