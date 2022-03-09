package rip.orbit.hcteams.map.kits.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.map.kits.DefaultKit;
import rip.orbit.hcteams.map.kits.editor.setup.KitEditorItemsMenu;

public class KitEditorItemsCommand {

    @Command(names = { "kitadmin editoritems" }, description = "Edit a kit's editor items", permission = "op")
    public static void execute(Player player, @Param(name = "kit") DefaultKit kit) {
        new KitEditorItemsMenu(kit).openMenu(player);
    }

}
