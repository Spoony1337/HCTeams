package rip.orbit.hcteams.commands.lff.menu.buttons;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.commands.lff.LFFCommand;
import rip.orbit.hcteams.commands.lff.menu.LFFMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class LFFCompleteButton extends Button {

    private LFFMenu lffMenu;


    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Click to Broadcast";
    }


    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>(Collections.singletonList(""));
        if(lffMenu.getSelected().isEmpty()) lore.add(ChatColor.RED + "You don't have a class selected!");
        else {
            lore.add(ChatColor.GOLD + "Selected Classes:");
            lffMenu.getSelected().forEach(lffKitButton -> lore.add(ChatColor.GRAY.toString() + ChatColor.BOLD + " » " + ChatColor.WHITE + lffKitButton));
        }
        return lore;
    }


    @Override
    public Material getMaterial(Player player) {
        return Material.EMERALD;
    }


    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if(lffMenu.getSelected().isEmpty()) {
            player.sendMessage(ChatColor.RED + "You don't have a class selected!");
            return;
        }

        player.closeInventory();
        LFFCommand.applyCooldown(player.getUniqueId());

        Bukkit.getOnlinePlayers().stream().filter(aPlayer -> HCF.getInstance().getToggleLFFMessageMap().isEnabled(aPlayer.getUniqueId())).forEach(aPlayer -> {
            aPlayer.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 43));
            aPlayer.sendMessage(player.getDisplayName() + ChatColor.WHITE + " is " + ChatColor.GOLD + "looking for a faction" + ChatColor.WHITE + "!");
            aPlayer.sendMessage(ChatColor.GRAY + " » " + ChatColor.WHITE + "Classes: " + ChatColor.GOLD + StringUtils.join(lffMenu.getSelected(), ChatColor.WHITE + ", "));
            aPlayer.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 43));
        });


    }
}
