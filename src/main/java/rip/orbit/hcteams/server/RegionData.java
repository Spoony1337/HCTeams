package rip.orbit.hcteams.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import rip.orbit.hcteams.team.Team;
import rip.orbit.hcteams.util.CC;

@AllArgsConstructor
@Data
public class RegionData {

    private RegionType regionType;
    private Team data;

    @Override
	public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RegionData)) {
            return (false);
        }

        RegionData other = (RegionData) obj;

        return (other.regionType == regionType && (data == null || other.data.equals(data)));
    }

    @Override
	public int hashCode() {
        return (super.hashCode());
    }

    public String getName(Player player) {
        if (data == null) {
            switch (regionType) {
                case WARZONE:
                    return (ChatColor.RED + "Warzone");
                case WILDNERNESS:
                    return (ChatColor.GRAY + "The Wilderness");
                default:
                    return (ChatColor.DARK_RED + "N/A");
            }
        }
        if (data.getName().equals("AbilityHill")) {
            return CC.YELLOW + "Ability Hill";
        }
        if (data.getName().equals("PumpkinPatch")) {
            return CC.GOLD + "Pumpkin Patch";
        }
        return (data.getName(player));
    }

}