package rip.orbit.hcteams.server.commands.betrayer;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.Betrayer;

import java.util.UUID;

import static org.bukkit.ChatColor.*;

public class BetrayerAddCommand {

    @Command(names = {"betrayer add"}, permission = "op")
    public static void betrayerAdd(Player sender, @Param(name = "player") UUID player, @Param(name = "reason", wildcard=true) String reason) {
        if (HCF.getInstance().getServerHandler().getBetrayer(player) == null) {
            Betrayer betrayer = new Betrayer(player, sender.getUniqueId(), reason);
            HCF.getInstance().getServerHandler().getBetrayers().add(betrayer);
            HCF.getInstance().getServerHandler().save();

            sender.sendMessage(GREEN + "Added " + RED + UUIDUtils.name(player) + GREEN + "'s betrayer tag for " + YELLOW +  reason + GREEN + ".");
        } else {
            sender.sendMessage(RED + UUIDUtils.name(player) + " is already a betrayer.");
        }
    }

}