package rip.orbit.hcteams.listener.kits;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.util.CC;


public class DisposibalSignListener implements Listener
{
    private String[] lines;
    private String[] error;

	
	public DisposibalSignListener(HCF plugin) {
        this.lines = new String[] { CC.translate(""), CC.translate("&c&lRecycle"), CC.translate(""), CC.translate("") };
        this.error = new String[] { CC.translate("&7&m-----------"), CC.translate("&c&lRecycle"), CC.translate("&7&m-----------"), CC.translate("&cError") };

	}
    public Inventory openMainInventory(Player player) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 36, CC.translate("&c&lRecycle"));

        player.openInventory(inv);
        return inv;
    } 
    @EventHandler
    public void onSignPlace(SignChangeEvent event) {
        if (event.getLine(0).equals("[Recycle Bin]")) {
            Player player = event.getPlayer();
            if (player.hasPermission("hcf.comamnd.*")) {
                for (int i = 0; i < this.lines.length; ++i) {
                    event.setLine(i, this.lines[i]);
                }
            }
            else {
                for (int i = 0; i < this.error.length; ++i) {
                    event.setLine(i, this.error[i]);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && block.getState() instanceof Sign) {
            Sign sign = (Sign)block.getState();
            for (int i = 0; i < this.lines.length; ++i) {
                if (!sign.getLine(i).equals(this.lines[i])) {
                    return;
                }
            }
            this.openMainInventory(player);
        }
    }
}
