package rip.orbit.hcteams.team.menu.button;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.commands.staff.TeamManageCommand;
import rip.orbit.hcteams.team.Team;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class OpenMuteMenuButton extends Button {

    private Team team;

    
    @Override
	public void clicked(Player player, int i, ClickType clickType) {
        TeamManageCommand.muteTeam(player, team);
    }

    
    @Override
	public String getName(Player player) {
        return "§7Mute Team";
    }

    
    @Override
	public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    
    @Override
	public byte getDamageValue(Player player) {
        return 0;
    }

    
    @Override
	public Material getMaterial(Player player) {
        return Material.CHEST;
    }
}
