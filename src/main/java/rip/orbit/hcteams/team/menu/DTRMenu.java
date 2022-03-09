package rip.orbit.hcteams.team.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.menu.button.DTRButton;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DTRMenu extends Menu {

    Team team;

    
    @Override
	public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 3) {
                buttons.put(i, new DTRButton(team, false));

            } else if (i == 5) {
                buttons.put(i, new DTRButton(team, true));

            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));

            }
        }

        return buttons;
    }

    
    @Override
	public String getTitle(Player player) {
        return "Manage DTR";
    }
}