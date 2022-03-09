package rip.orbit.hcteams.settings.menu;

import com.google.common.collect.Maps;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.settings.Setting;

import java.util.Map;

public class SettingsMenu extends Menu {


    @Override
    public String getTitle(Player player) {
        return "Options";
    }


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(2, Setting.CLAIMONSB.toButton());
        buttons.put(4, Setting.ABILITY_CD.toButton());
        buttons.put(6, Setting.FOUND_DIAMONDS.toButton());

        buttons.put(11, Setting.TIPS.toButton());
        buttons.put(13, Setting.PUBLIC_CHAT.toButton());
        buttons.put(15, Setting.FACTION_INVITES.toButton());
        buttons.put(22, Setting.GENERATOR.toButton());

        if(player.hasPermission("foxtrot.staff")) {
            buttons.put(0, Setting.SCOREBOARD_STAFF_BOARD.toButton());
        }


        return buttons;
    }

}