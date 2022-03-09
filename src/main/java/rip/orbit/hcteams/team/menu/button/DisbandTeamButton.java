package rip.orbit.hcteams.team.menu.button;

import lombok.AllArgsConstructor;
import net.frozenorb.qlib.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.team.commands.ForceDisbandCommand;
import rip.orbit.hcteams.team.menu.ConfirmMenu;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DisbandTeamButton extends Button {

    private Team team;


    @Override
	public void clicked(Player player, int i, ClickType clickType) {
        new ConfirmMenu("Disband?", (b) -> {
            if (b) {
                ForceDisbandCommand.forceDisband(player, team);
            }
        }).openMenu(player);
    }


    @Override
	public String getName(Player player) {
        return "§c§lDisband Team";
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
        return Material.TNT;
    }
}
