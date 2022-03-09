package rip.orbit.hcteams.util;

import net.frozenorb.qlib.util.ItemUtils;
import net.minecraft.util.com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.team.Team;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StringUtils {
    public static void handleShopSign(Sign sign, Player player) {
        Team user = HCF.getInstance().getTeamHandler().getTeam(player.getUniqueId());
        ItemStack itemStack = sign.getLine(2).contains("Crowbar") ? (new Crowbar()).getItemIfPresent() : ItemUtils.get(sign.getLine(2).toLowerCase().replace(" ", ""));
        if (itemStack == null) {
            System.err.println(sign.getLine(2).toLowerCase().replace(" ", ""));
            return;
        }
        if (sign.getLine(0).toLowerCase().contains("buy")) {
            int price;
            int amount;
            try {
                price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount = Integer.parseInt(sign.getLine(1));
            } catch (NumberFormatException e) {
                return;
            }
            if (user.getBalance() >= price) {
                if (Double.isNaN(user.getBalance())) {
                    user.setBalance(0.0D);
                    player.sendMessage("&cYour balance was fucked, but we unfucked it.");
                    return;
                }
                if (player.getInventory().firstEmpty() != -1) {
                    user.setBalance(user.getBalance() - price);
                    itemStack.setAmount(amount);
                    player.getInventory().addItem(new ItemStack[] { itemStack });
                    player.updateInventory();
                    showSignPacket(player, sign, new String[] { "&aBOUGHT&r " + amount, "for &a$" + NumberFormat.getNumberInstance(Locale.US).format(price), "New Balance:", "&a$" + NumberFormat.getNumberInstance(Locale.US).format(user.getBalance()) });
                } else {
                    showSignPacket(player, sign, new String[] { "&c&lError!", "", "&cNo space", "&cin inventory!" });
                }
            } else {
                showSignPacket(player, sign, new String[] { "&cInsufficient", "&cfunds for", sign.getLine(2), sign.getLine(3) });
            }
        } else if (sign.getLine(0).toLowerCase().contains("sell")) {
            int amount2;
            double pricePerItem;
            try {
                int price2 = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount2 = Integer.parseInt(sign.getLine(1));
                pricePerItem = (price2 / amount2);
            } catch (NumberFormatException e2) {
                return;
            }
            int amountInInventory = Math.min(amount2, countItems(player, itemStack.getType(), itemStack.getDurability()));
            if (amountInInventory == 0) {
                showSignPacket(player, sign, new String[] { "&cYou do not", "&chave any", sign.getLine(2), "&con you!" });
            } else {
                int totalPrice = (int)(amountInInventory * pricePerItem);
                removeItem(player, itemStack, amountInInventory);
                player.updateInventory();
                user.setBalance(user.getBalance() + totalPrice);
                showSignPacket(player, sign, new String[] { "&aSOLD&r " + amountInInventory, "for &a$" + NumberFormat.getNumberInstance(Locale.US).format(totalPrice), "New Balance:", "&a$" + NumberFormat.getNumberInstance(Locale.US).format(user.getBalance()) });
            }
        }
    }

    public static void removeItem(Player p, ItemStack it, int amount) {
        boolean specialDamage = (it.getType().getMaxDurability() == 0);
        for (int a = 0; a < amount; a++) {
            for (ListIterator<ItemStack> listIterator = p.getInventory().iterator(); listIterator.hasNext(); ) {
                ItemStack i = listIterator.next();
                if (i != null && i.getType() == it.getType() && (!specialDamage || it.getDurability() == i.getDurability())) {
                    if (i.getAmount() == 1) {
                        p.getInventory().clear(p.getInventory().first(i));
                        break;
                    }
                    i.setAmount(i.getAmount() - 1);
                    break;
                }
            }
        }
    }

    public static void showSignPacket(Player player, Sign sign, String... lines) {
        player.sendSignChange(sign.getLocation(), lines);
        if (showSignTasks.containsKey(sign))
            ((BukkitRunnable)showSignTasks.remove(sign)).cancel();
        BukkitRunnable br = new BukkitRunnable() {
            @Override
			public void run() {
                sign.update();
                StringUtils.showSignTasks.remove(sign);
            }
        };
        showSignTasks.put(sign, br);
        br.runTaskLaterAsynchronously((Plugin) HCF.getInstance(), 90L);
    }

    public static int countItems(Player player, Material material, int damageValue) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] items = playerInventory.getContents();
        int amount = 0;
        for (ItemStack item : items) {
            if (item != null) {
                boolean specialDamage = (material.getMaxDurability() == 0);
                if (item.getType() != null && item.getType() == material && (!specialDamage || item.getDurability() == (short)damageValue))
                    amount += item.getAmount();
            }
        }
        return amount;
    }

    public static boolean isInvFull(Player player) {
        return (player.getInventory().firstEmpty() == -1);
    }

    public static Integer getPing(Player player) {
        return Integer.valueOf((((CraftPlayer)player).getHandle()).ping);
    }

    public static String formatTime(long time, FormatType type) {
        switch (type) {
            case MILLIS_TO_SECONDS:
                return "" + (Math.round((float)time / 1000.0F * 10.0F) / 10.0F);
            case MILLIS_TO_MINUTES:
                return secondsToMinutes((int)(time / 1000L));
            case MILLIS_TO_HOURS:
                return secondsToHours((int)(time / 1000L));
            case SECONDS_TO_MINUTES:
                return secondsToMinutes((int)time);
            case SECONDS_TO_HOURS:
                return secondsToHours((int)time);
        }
        return "";
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int parseSeconds(String value) {
        if (isInteger(value))
            return Math.abs(Integer.parseInt(value));
        if (value.equalsIgnoreCase("0s"))
            return 0;
        value = value.toLowerCase();
        int seconds = 0;
        for (TimeFormat format : TimeFormat.values()) {
            if (value.contains(format.timeChar)) {
                String[] split = value.split(format.timeChar);
                if (isInteger(split[0])) {
                    seconds += (int)(Math.abs(Integer.parseInt(split[0])) * format.seconds);
                    if (split.length > 1)
                        value = split[1];
                }
            }
        }
        return (seconds == 0) ? -1 : seconds;
    }

    private static String secondsToMinutes(int seconds) {
        if (seconds < 60)
            return "00:" + ((seconds < 10) ? ("0" + seconds) : ("" + seconds));
        int secondsModulo = seconds % 60;
        int minutes = seconds / 60;
        return ((minutes < 10) ? ("0" + minutes) : Integer.valueOf(minutes)) + ":" + ((secondsModulo < 10) ? ("0" + secondsModulo) : Integer.valueOf(secondsModulo));
    }

    private static String secondsToHours(int seconds) {
        if (seconds < 60)
            return "00:00:" + ((seconds < 10) ? ("0" + seconds) : ("" + seconds));
        int secondsModulo = seconds % 60;
        int minutes = seconds / 60;
        String secondsDisplay = (secondsModulo < 10) ? ("0" + secondsModulo) : ("" + secondsModulo);
        if (minutes < 60)
            return "00:" + ((minutes < 10) ? ("0" + minutes) : ("" + minutes)) + ":" + secondsDisplay;
        int minutesModulo = minutes % 60;
        int hours = minutes / 60;
        return ((hours < 10) ? ("0" + hours) : Integer.valueOf(hours)) + ":" + ((minutesModulo < 10) ? ("0" + minutesModulo) : ("" + minutesModulo)) + ":" + secondsDisplay;
    }


    public static String color(String s) {
        return s.replaceAll("&", "&");
    }

    public static ItemStack setItemTitle(ItemStack item, String title) {
        Preconditions.checkNotNull(item);
        Preconditions.checkNotNull(title);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(title);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack setItemLore(ItemStack item, List<String> lore) {
        Preconditions.checkNotNull(item, lore);
        Preconditions.checkNotNull(lore);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    private static HashMap<Sign, BukkitRunnable> showSignTasks = new HashMap<>();

    public enum TimeFormat {
        DAY("d", TimeUnit.DAYS.toSeconds(1L)),
        HOUR("h", TimeUnit.HOURS.toSeconds(1L)),
        MINUTE("m", TimeUnit.MINUTES.toSeconds(1L)),
        SECOND("s", 1L);

        private String timeChar;

        private long seconds;

        TimeFormat(String timeChar, long seconds) {
            this.timeChar = timeChar;
            this.seconds = seconds;
        }

        public String getTimeChar() {
            return this.timeChar;
        }

        public long getSeconds() {
            return this.seconds;
        }
    }

    public enum FormatType {
        MILLIS_TO_SECONDS, MILLIS_TO_MINUTES, MILLIS_TO_HOURS, SECONDS_TO_MINUTES, SECONDS_TO_HOURS
	}
}
