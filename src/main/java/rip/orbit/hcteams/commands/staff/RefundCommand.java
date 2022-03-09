package rip.orbit.hcteams.commands.staff;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import rip.orbit.hcteams.HCF;

import java.util.List;

public class RefundCommand {

    @Command(names = {"refund", "invrestore"}, permission = "foxtrot.staff")
    public static void refundCommand(CommandSender sender, @Param(name = "player") Player target, @Param(name = "reason", wildcard = true) String reason){
        if (reason.equals(".")){
            sender.sendMessage(ChatColor.RED + "Invalid Reason.");
            return;
        }

        HCF.getInstance().getServer().getScheduler().runTaskAsynchronously(HCF.getInstance(), () -> {
            qLib.getInstance().runRedisCommand((redis) -> {
               if (!redis.exists("lastInv:contents:" + target.getUniqueId())){
                   sender.sendMessage(ChatColor.RED + "No last inventory recorded for " + FrozenUUIDCache.name(target.getUniqueId()));
                   return null;
               }

                ItemStack[] contents = qLib.PLAIN_GSON.fromJson(redis.get("lastInv:contents:" + target.getUniqueId()), ItemStack[].class);
                ItemStack[] armor = qLib.PLAIN_GSON.fromJson(redis.get("lastInv:armorContents:" + target.getUniqueId()), ItemStack[].class);

                cleanLoot(contents);
                cleanLoot(armor);
                HCF.getInstance().getServer().getScheduler().runTaskAsynchronously(HCF.getInstance(), () -> {
                    target.getInventory().setContents(contents);
                    target.getInventory().setArmorContents(armor);
                    target.updateInventory();

                    sender.sendMessage(ChatColor.GREEN + "Loaded " + FrozenUUIDCache.name(target.getUniqueId()) + "'s last inventory.");
                    HCF.getInstance().getDiscordLogger().logRefund(target.getName(), sender.getName(), reason);
                });

                return null;
            });
        });
    }

    public static void cleanLoot(ItemStack[] stack) {
        for (ItemStack item : stack) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                ItemMeta meta = item.getItemMeta();

                List<String> lore = item.getItemMeta().getLore();
                lore.remove(ChatColor.DARK_GRAY + "PVP Loot");
                meta.setLore(lore);

                item.setItemMeta(meta);
            }
        }
    }
}
