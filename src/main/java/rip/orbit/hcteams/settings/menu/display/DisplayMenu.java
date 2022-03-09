package rip.orbit.hcteams.settings.menu.display;

import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.settings.Setting;
import rip.orbit.hcteams.settings.menu.color.ColorMenu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayMenu extends Menu {

    public DisplayMenu() {
        super(ChatColor.BLUE + "Edit your Display preferences");
        setAutoUpdate(true);
        setUpdateAfterClick(true);
    }

    
    @Override
	public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(1, Setting.TAB_LIST.toButton());
        buttons.put(3, Setting.FOUND_DIAMONDS.toButton());

        buttons.put(7, new Button() {
            
            @Override
			public String getName(Player player) {
                return org.bukkit.ChatColor.LIGHT_PURPLE + "Change your Colors";
            }

            
            @Override
			public List<String> getDescription(Player player) {
                return Arrays.asList("", org.bukkit.ChatColor.BLUE + "Click to modify your", org.bukkit.ChatColor.BLUE + "nametag colors.", "", org.bukkit.ChatColor.GRAY + "[TIP]", org.bukkit.ChatColor.GRAY + "- " + org.bukkit.ChatColor.ITALIC + "Left Click to go forward.", org.bukkit.ChatColor.GRAY + "- " + org.bukkit.ChatColor.ITALIC + "Right Click to go backward.");
            }

            
            @Override
			public Material getMaterial(Player player) {
                return Material.NAME_TAG;
            }

            
            @Override
			public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new ColorMenu().openMenu(player);
            }
        });
        return buttons;
    }
}