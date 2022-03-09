package rip.orbit.hcteams.tab;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TabListMode {

    DETAILED("Detailed"),
    DETAILED_WITH_FACTION_INFO("Detailed w/ Team List"),
    VANILLA("Vanilla");

    private String name;

    public String getName() {
        return name;
    }

}
