package rip.orbit.hcteams.team.commands.pvp;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.HCF;

import java.util.UUID;

public class PvPSetLivesCommand {

    @Command(names={ "pvptimer setlives", "pvp setlives", "pvptimer setlives", "pvp setlives" }, permission="foxtrot.setlives")
    public static void pvpSetLives(Player sender, @Param(name="player") UUID player, @Param(name="amount") int amount) {
        HCF.getInstance().getLivesMap().setLives(player, amount);
        sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "'s life count to " + amount + ".");

    }

}