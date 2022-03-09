package rip.orbit.hcteams.settings.menu.display;

import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.List;

public class DisplayButton extends Button {

    @Override
	public String getName(Player player) {
        return ChatColor.LIGHT_PURPLE + "Change Display Settings";
    }


    @Override
	public List<String> getDescription(Player player) {
        return Arrays.asList("", ChatColor.BLUE + "Click to modify more", ChatColor.BLUE + "display based colors.");
    }


    @Override
	public Material getMaterial(Player player) {
        return Material.PAINTING;
    }


    @Override
	public void clicked(Player player, int slot, ClickType clickType) {
        new DisplayMenu().openMenu(player);
    }
}
