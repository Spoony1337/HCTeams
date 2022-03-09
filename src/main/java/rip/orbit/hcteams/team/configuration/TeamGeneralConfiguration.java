//package rip.orbit.hcteams.team.configuration;
//
//import lombok.Getter;
//import rip.orbit.hcteams.HCF;
//import net.frozenorb.qlib.configuration.Configuration;
//import net.frozenorb.qlib.configuration.annotations.ConfigData;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TeamGeneralConfiguration extends Configuration {
//
//    @ConfigData(path = "teams.disallowed_names")
//    @Getter
//    private static List<String> disallowedNames = new ArrayList<>();
//
//    public TeamGeneralConfiguration() {
//        super(HCF.getInstance(), "config.yml", "./plugins/Teams/");
//        disallowedNames.add("glowstone");
//        disallowedNames.add("miniend");
//        disallowedNames.add("warzone");
//        disallowedNames.add("spawn");
//        disallowedNames.add("end");
//        disallowedNames.add("theend");
//        disallowedNames.add("citadel");
//        load();
//        save();
//    }
//}
