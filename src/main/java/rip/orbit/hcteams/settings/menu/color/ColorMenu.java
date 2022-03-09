package rip.orbit.hcteams.settings.menu.color;

import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.settings.menu.color.buttons.ColorButton;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ColorMenu extends Menu {

    public ColorMenu() {
        super(ChatColor.BLUE + "Edit your Colors");
        setAutoUpdate(true);
        setUpdateAfterClick(true);
    }


    @Override
	public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (ColorTypes value : ColorTypes.values()) buttonMap.put(atomicInteger.getAndAdd(2), new ColorButton(value));
        return buttonMap;
    }
}
