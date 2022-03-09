package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;

public class SetWorldBufferCommand {

    @Command(names={ "SetWorldBuffer" }, permission="op")
    public static void setWorldBuffer(Player sender, @Param(name="worldBuffer") int newBuffer) {
        HCF.getInstance().getMapHandler().setWorldBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The world buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            
            @Override
			public void run() {
                HCF.getInstance().getMapHandler().saveWorldBuffer();
            }

        }.runTaskAsynchronously(HCF.getInstance());
    }

}
