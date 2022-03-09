package rip.orbit.hcteams.util.menu.page;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import rip.orbit.hcteams.util.menu.Button;
import rip.orbit.hcteams.util.object.ItemBuilder;

public class PageButton extends Button {

    private int mod;
    private PagedMenu menu;

    public PageButton(int mod, PagedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }


    @Override
    public ItemStack getItem(Player player) {
        if (this.hasNext(player)) {
            return new ItemBuilder(Material.ARROW)
                    .name(mod > 0 ? "Next Page" : "Go Back")
                    .build();
        } else {
            return new ItemBuilder(Material.ARROW)
                    .name(mod > 0 ? "Last Page" : "First Page")
                    .build();
        }
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (hasNext(player)) {
            this.menu.modPage(player, mod);
        }
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }

}
