package rip.orbit.hcteams.map.game;

import lombok.experimental.UtilityClass;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.util.item.InventoryUtils;

@UtilityClass
public class GameUtils {

    public static void resetPlayer(Player player) {
        InventoryUtils.resetInventoryNow(player);

        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setItem(4, GameItems.LEAVE_EVENT);
        player.updateInventory();
    }

}
