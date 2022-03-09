package rip.orbit.hcteams.util;

import com.google.common.base.Optional;
import net.minecraft.util.com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Crowbar {
    public static int MAX_SPAWNER_USES = 1;

    public static int MAX_END_FRAME_USES = 5;

    public static Material CROWBAR_TYPE;

    private static String CROWBAR_NAME;

    private static String SPAWNER_USE_TAG = "Spawner Uses";

    private static String END_FRAME_USE_TAG = "End Frame Uses";

    private static String LORE_FORMAT;

    private ItemStack stack;

    private int endFrameUses;

    private int spawnerUses;

    private boolean needsMetaUpdate;

    public static Optional<Crowbar> fromStack(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta())
            return Optional.absent();
        ItemMeta meta = stack.getItemMeta();
        if (!meta.hasDisplayName() || !meta.hasLore() || !meta.getDisplayName().equals(CROWBAR_NAME))
            return Optional.absent();
        Crowbar crowbar = new Crowbar();
        List<String> loreList = meta.getLore();
        for (String lore : loreList) {
            lore = ChatColor.stripColor(lore);
            for (int length = lore.length(), i = 0; i < length; i++) {
                char character = lore.charAt(i);
                if (Character.isDigit(character)) {
                    int amount = Integer.parseInt(String.valueOf(character));
                    if (lore.startsWith("End Frame Uses")) {
                        crowbar.setEndFrameUses(amount);
                        break;
                    }
                    if (lore.startsWith("Spawner Uses")) {
                        crowbar.setSpawnerUses(amount);
                        break;
                    }
                }
            }
        }
        return Optional.of(crowbar);
    }

    public Crowbar() {
        this(1, 5);
    }

    public Crowbar(int spawnerUses, int endFrameUses) {
        this.stack = new ItemStack(CROWBAR_TYPE, 1);
        Preconditions.checkArgument((spawnerUses > 0 || endFrameUses > 0), "Cannot create a crowbar with empty uses");
        setSpawnerUses(Math.min(1, spawnerUses));
        setEndFrameUses(Math.min(5, endFrameUses));
    }

    public int getEndFrameUses() {
        return this.endFrameUses;
    }

    public void setEndFrameUses(int uses) {
        if (this.endFrameUses != uses) {
            this.endFrameUses = Math.min(5, uses);
            this.needsMetaUpdate = true;
        }
    }

    public int getSpawnerUses() {
        return this.spawnerUses;
    }

    public void setSpawnerUses(int uses) {
        if (this.spawnerUses != uses) {
            this.spawnerUses = Math.min(1, uses);
            this.needsMetaUpdate = true;
        }
    }

    public ItemStack getItemIfPresent() {
        Optional<ItemStack> optional = toItemStack();
        return optional.isPresent() ? (ItemStack)optional.get() : new ItemStack(Material.AIR, 1);
    }

    public Optional<ItemStack> toItemStack() {
        if (this.needsMetaUpdate) {
            double maxDurability = CROWBAR_TYPE.getMaxDurability(), curDurability = maxDurability;
            double increment = curDurability / 6.0D;
            curDurability -= increment * (this.spawnerUses + this.endFrameUses);
            if (Math.abs(curDurability - maxDurability) == 0.0D)
                return Optional.absent();
            ItemMeta meta = this.stack.getItemMeta();
            meta.setDisplayName(CROWBAR_NAME);
            meta.setLore(Arrays.asList(new String[] { String.format(LORE_FORMAT, new Object[] { "Spawner Uses", Integer.valueOf(this.spawnerUses), Integer.valueOf(1) }), String.format(LORE_FORMAT, new Object[] { "End Frame Uses", Integer.valueOf(this.endFrameUses), Integer.valueOf(5) }) }));
            this.stack.setItemMeta(meta);
            this.stack.setDurability((short)(int)curDurability);
            this.needsMetaUpdate = false;
        }
        return Optional.of(this.stack);
    }

    static {
        CROWBAR_TYPE = Material.DIAMOND_HOE;
        CROWBAR_NAME = ChatColor.RED + "Crowbar";
        LORE_FORMAT = ChatColor.GRAY + "%1$s: " + ChatColor.YELLOW + "%2$s/%3$s";
    }
}

