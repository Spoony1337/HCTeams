package rip.orbit.hcteams.team.commands.pvp;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import rip.orbit.hcteams.HCF;

import java.util.UUID;

public class PvPLivesCommand {

    @Command(names={ "pvptimer lives", "pvp lives" }, permission="")
    public static void pvpLives(CommandSender sender, @Param(name="player", defaultValue="self") UUID player) {
        String name = UUIDUtils.name(player);
        sender.sendMessage(ChatColor.GOLD + name + "'s Lives: " + ChatColor.WHITE + HCF.getInstance().getLivesMap().getLives(player));
    }

}