package rip.orbit.hcteams.settings.menu.button;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.settings.Setting;
import rip.orbit.hcteams.tab.TabListMode;

import java.util.List;

@AllArgsConstructor
public class SettingButton extends Button {

    private Setting setting;


    @Override
	public String getName(Player player) {
        return setting.getName();
    }


    @Override
	public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add("");
        description.addAll(setting.getDescription());
        description.add("");

        if (setting != Setting.TAB_LIST) {
            if (setting.isEnabled(player)) {
                description.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "  ► " + setting.getEnabledText());
                description.add("" + setting.getDisabledText());
            } else {
                description.add("" + setting.getEnabledText());
                description.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "  ► " + setting.getDisabledText());
            }
        } else {
            TabListMode current = HCF.getInstance().getTabListModeMap().getTabListMode(player.getUniqueId());

            for (TabListMode mode : TabListMode.values()) {
                if (mode != current) {
                    description.add("    " + ChatColor.GRAY + mode.getName());
                } else {
                    description.add(ChatColor.BLUE.toString() + ChatColor.BOLD + "  ► " + ChatColor.GREEN + mode.getName());
                }
            }
        }

        return description;
    }


    @Override
	public Material getMaterial(Player player) {
        return setting.getIcon();
    }


    @Override
	public byte getDamageValue(Player player) {
        return 0;
    }


    @Override
	public void clicked(Player player, int i, ClickType clickType) {
        setting.toggle(player);
        player.updateInventory();
    }

    public static TabListMode next(TabListMode current) {
        switch (current) {
            case DETAILED:
                return TabListMode.VANILLA;
            case DETAILED_WITH_FACTION_INFO:
                return TabListMode.DETAILED;
            case VANILLA:
                return TabListMode.DETAILED_WITH_FACTION_INFO;
            default:
                return TabListMode.DETAILED;
        }
    }

}
