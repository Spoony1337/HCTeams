package rip.orbit.hcteams.settings.menu.color.buttons;

import net.frozenorb.qlib.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.HCF;
import rip.orbit.hcteams.settings.menu.color.ColorTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorButton extends Button {

    private ColorTypes colorTypes;

    private List<ChatColor> colorList = new ArrayList<>(Arrays.asList(
            ChatColor.DARK_BLUE, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.WHITE, ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE,
            ChatColor.DARK_GRAY, ChatColor.GRAY, ChatColor.DARK_RED, ChatColor.RED, ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.GOLD
    ));

    public ColorButton(ColorTypes colorTypes) {
        this.colorTypes = colorTypes;
    }

    
    @Override
    public String getName(Player player) {
        return ChatColor.LIGHT_PURPLE + colorTypes.getDisplayName();
    }

    
    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>(Arrays.asList("", ChatColor.BLUE + "Click to edit your " + ChatColor.BLUE + colorTypes.getDisplayName() + " color.", ""));

        colorList.forEach(chatColor -> lore.add((getColor(player) == chatColor ? ChatColor.BLUE.toString() + ChatColor.BOLD + " » " : "") + chatColor + "Example"));
        return lore;
    }

    
    @Override
    public Material getMaterial(Player player) {
        return Material.STAINED_GLASS_PANE;
    }

    
    @Override
    public byte getDamageValue(Player player) {
        return getColor(getColor(player).getChar()).getWoolData();
    }

    
    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        setColor(player, ((clickType != ClickType.RIGHT)));
//        FrozenNametagHandler.reloadOthersFor(player);
//        FrozenNametagHandler.reloadPlayer(player);
    }

    public ChatColor getColor(Player player) {
        switch(colorTypes) {
            case TEAM: {
                return HCF.getInstance().getTeamColorMap().getColor(player.getUniqueId());
            }
            case ALLY: {
                return HCF.getInstance().getAllyColorMap().getColor(player.getUniqueId());
            }
            case ARCHER_TAG: {
                return HCF.getInstance().getArcherTagColorMap().getColor(player.getUniqueId());
            }
            case FOCUS: {
                return HCF.getInstance().getFocusColorMap().getColor(player.getUniqueId());
            }
            default: {
                return HCF.getInstance().getEnemyColorMap().getColor(player.getUniqueId());
            }
        }
    }

    private ChatColor getNextColor(Player player, boolean forward) {
        int pos = colorList.indexOf(getColor(player)) + (forward ? 1 : -1);

        if(pos >= colorList.size()) pos = 0;
        if(pos < 0) pos = colorList.size() - 1;

        return colorList.get(pos);
    }

    private void setColor(Player player, boolean forward) {
        ChatColor input = getNextColor(player, forward);
        switch(colorTypes) {
            case TEAM: {
                HCF.getInstance().getTeamColorMap().setColor(player.getUniqueId(), input);
                break;
            }
            case ALLY: {
                HCF.getInstance().getAllyColorMap().setColor(player.getUniqueId(), input);
                break;
            }
            case ARCHER_TAG: {
                HCF.getInstance().getArcherTagColorMap().setColor(player.getUniqueId(), input);
                break;
            }
            case FOCUS: {
                HCF.getInstance().getFocusColorMap().setColor(player.getUniqueId(), input);
                break;
            }
            case ENEMY: {
                HCF.getInstance().getEnemyColorMap().setColor(player.getUniqueId(), input);
                break;
            }
            default: {
                player.sendMessage(ChatColor.RED + "Something went wrong.");
                break;
            }
        }
    }

    private DyeColor getColor(char str) {
        ChatColor color = ChatColor.getByChar(str);
        switch (color) {
            case DARK_BLUE:
            case BLUE: {
                return DyeColor.BLUE;
            }
            case DARK_GREEN: {
                return DyeColor.GREEN;
            }
            case DARK_AQUA:
            case AQUA: {
                return DyeColor.CYAN;
            }
            case DARK_RED:
            case RED: {
                return DyeColor.RED;
            }
            case DARK_PURPLE: {
                return DyeColor.PURPLE;
            }
            case GOLD: {
                return DyeColor.ORANGE;
            }
            case GRAY:
            case DARK_GRAY: {
                return DyeColor.GRAY;
            }
            case GREEN: {
                return DyeColor.LIME;
            }
            case LIGHT_PURPLE: {
                return DyeColor.PINK;
            }
            case YELLOW: {
                return DyeColor.YELLOW;
            }
            case WHITE: {
                return DyeColor.WHITE;
            }
            default: {
                return DyeColor.BLACK;
            }
        }
    }

}
