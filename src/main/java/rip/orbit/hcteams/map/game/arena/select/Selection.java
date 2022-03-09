package rip.orbit.hcteams.map.game.arena.select;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.frozenorb.qlib.cuboid.Cuboid;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import rip.orbit.hcteams.HCF;

@Data
@AllArgsConstructor
public class Selection {

    public static final ItemStack SELECTION_WAND = ItemBuilder.of(Material.GOLD_AXE).name("&aSelection Wand").build();
    public static final String SELECTION_METADATA_KEY = "selection";

    private Location loc1, loc2;

    private Selection() {}

    public Cuboid getCuboid() {
        if (!isComplete()) return null;

        return new Cuboid(loc1, loc2);
    }

    public boolean isComplete() {
        return loc1 != null && loc2 != null;
    }

    public static Selection getOrCreateSelection(Player player) {
        if (player.hasMetadata(SELECTION_METADATA_KEY)) {
            return (Selection) player.getMetadata(SELECTION_METADATA_KEY).get(0).value();
        }

        Selection selection = new Selection();
        player.setMetadata(SELECTION_METADATA_KEY, new FixedMetadataValue(HCF.getInstance(), selection));

        return selection;
    }

}
