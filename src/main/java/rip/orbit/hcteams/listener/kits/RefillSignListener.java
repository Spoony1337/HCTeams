package rip.orbit.hcteams.listener.kits;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import rip.orbit.hcteams.HCF;

public class RefillSignListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if ((HCF.getInstance().getMapHandler().isKitMap() || HCF.getInstance().getServerHandler().isVeltKitMap()) && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            if (sign.getLine(1).equalsIgnoreCase(ChatColor.DARK_RED + "- Refill -")){
                    Inventory refillSign = Bukkit.createInventory((InventoryHolder) null, 36, ChatColor.DARK_RED + "Refill Inventory");
                    Potion pot = new Potion(PotionType.INSTANT_HEAL);
                    pot.setSplash(true);
                    pot.setLevel(2);
                    ItemStack heal = pot.toItemStack(1);
                    Potion speedpot = new Potion(PotionType.SPEED);
                    speedpot.setLevel(2);
                    ItemStack speed = speedpot.toItemStack(1);
                    ItemStack pork = new ItemStack(Material.BAKED_POTATO, 64);
                    ItemStack pearl = new ItemStack(Material.ENDER_PEARL, 16);
                    ItemStack goldensword = new ItemStack(Material.GOLD_SWORD, 1);
                    for (int i = 0; i < refillSign.getSize(); ++i){
                        refillSign.setItem(i, heal);
                    }
                    refillSign.setItem(0, pork);
                    refillSign.setItem(1, pearl);
                    refillSign.setItem(9, goldensword);
                    refillSign.setItem(18, goldensword);
                    refillSign.setItem(27, goldensword);
                    refillSign.setItem(7, speed);
                    refillSign.setItem(8, speed);
                    refillSign.setItem(16, speed);
                    refillSign.setItem(17, speed);
                    refillSign.setItem(25, speed);
                    refillSign.setItem(26, speed);
                    player.openInventory(refillSign);
                }
            }
        }
}
