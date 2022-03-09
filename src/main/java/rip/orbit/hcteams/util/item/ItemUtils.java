package rip.orbit.hcteams.util.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.function.Predicate;

/*
* Made By LBuddyBoy
* */
public class ItemUtils {

	public static boolean hasLore(ItemStack stack) {
		return stack != null && stack.getItemMeta() != null && stack.getItemMeta().getLore() != null && !stack.getItemMeta().getLore().isEmpty();
	}

	public static boolean hasDisplayName(ItemStack stack) {
		return stack != null && stack.getItemMeta() != null && stack.getItemMeta().getDisplayName() != null && !stack.getItemMeta().getDisplayName().isEmpty();
	}


	public static void tryFit(Player p, ItemStack item) {
		PlayerInventory inv = p.getInventory();
		boolean canfit = false;
		for (int i = 0; i < inv.getSize(); ++i) {
			if (inv.getItem(i) == null || inv.getItem(i) != null && inv.getItem(i).getType() == Material.AIR) {
				canfit = true;
				inv.addItem(item);
				break;
			}
		}
		if (!canfit) {
			p.getWorld().dropItemNaturally(p.getLocation(), item);
		}
	}
	/**
	 * Checks if a {@link ItemStack} is an instant heal potion (if its type is {@link PotionType#INSTANT_HEAL})
	 */
	public static final Predicate<ItemStack> INSTANT_HEAL_POTION_PREDICATE = item -> {
		if (item.getType() != Material.POTION) {
			return false;
		}

		PotionType potionType = Potion.fromItemStack(item).getType();
		return potionType == PotionType.INSTANT_HEAL;
	};

	/**
	 * Checks if a {@link ItemStack} is a bowl of mushroom soup (if its type is {@link Material#MUSHROOM_SOUP})
	 */
	public static final Predicate<ItemStack> SOUP_PREDICATE = item -> item.getType() == Material.MUSHROOM_SOUP;

	/**
	 * Checks if a {@link ItemStack} is a debuff potion
	 */
	public static final Predicate<ItemStack> DEBUFF_POTION_PREDICATE = item -> {
		if (item.getType() == Material.POTION) {
			PotionType type = Potion.fromItemStack(item).getType();
			return type == PotionType.WEAKNESS || type == PotionType.SLOWNESS
					|| type == PotionType.POISON || type == PotionType.INSTANT_DAMAGE;
		} else {
			return false;
		}
	};

	/**
	 * Checks if a {@link ItemStack} is edible (if its type passes {@link Material#isEdible()})
	 */
	public static final Predicate<ItemStack> EDIBLE_PREDICATE = item -> item.getType().isEdible();

	/**
	 * Returns the number of stacks of items matching the predicate provided.
	 *
	 * @param items ItemStack array to scan
	 * @param predicate The predicate which will be applied to each non-null temStack.
	 * @return The amount of ItemStacks which matched the predicate, or 0 if {@code items} was null.
	 */
	public static int countStacksMatching(ItemStack[] items, Predicate<ItemStack> predicate) {
		if (items == null) {
			return 0;
		}

		int amountMatching = 0;

		for (ItemStack item : items) {
			if (item != null && predicate.test(item)) {
				amountMatching++;
			}
		}

		return amountMatching;
	}

	public static boolean hasEmptyInventory(Player player) {
		for (ItemStack itemStack : player.getInventory().getContents()) {
			if (itemStack != null && itemStack.getType() != Material.AIR) {
				return false;
			}
		}

		for (ItemStack itemStack : player.getInventory().getArmorContents()) {
			if (itemStack != null && itemStack.getType() != Material.AIR) {
				return false;
			}
		}

		return true;
	}
}
