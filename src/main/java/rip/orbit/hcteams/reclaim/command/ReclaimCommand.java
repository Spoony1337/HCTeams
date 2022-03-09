package rip.orbit.hcteams.reclaim.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import rip.orbit.gravity.profile.Profile;
import rip.orbit.gravity.util.uuid.UniqueIDCache;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.CC;

import java.util.UUID;

public class ReclaimCommand {

    @Command(names = {"reclaim"}, permission = "")
    public static void execute(Player player) {

        if (HCF.getInstance().getReclaimHandler().getReclaimMap().isToggled(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have already reclaimed this map.");
            return;
        }


        if (HCF.getInstance().getReclaimConfig().getConfiguration().getStringList("groups." + Profile.getByUuid(player.getUniqueId()).getActiveRank().getDisplayName()) == null || !HCF.getInstance().getReclaimConfig().getConfiguration().contains("groups." +Profile.getByUuid(player.getUniqueId()).getActiveRank().getDisplayName())) {
            player.sendMessage(ChatColor.RED + "It appears there is no reclaim found for your rank.");
            return;
        }

        for (String command : HCF.getInstance().getReclaimConfig().getConfiguration().getStringList("groups." + Profile.getByUuid(player.getUniqueId()).getActiveRank().getDisplayName() + ".commands")) {
            HCF.getInstance().getServer().dispatchCommand(HCF.getInstance().getServer().getConsoleSender(), command.replace("%player%",player.getName()));
        }

        HCF.getInstance().getReclaimHandler().getReclaimMap().setToggled(player.getUniqueId(),true);
    }

    @Command(names = "resetreclaim", permission = "op")
    public static void resetredeem(CommandSender sender, @Param(name = "player") UUID target) {
        sender.sendMessage(CC.translate("&aReset " + UniqueIDCache.name(target) + "'s Reclaim"));
        HCF.getInstance().getReclaimHandler().getReclaimMap().setToggled(target, false);
    }

}
