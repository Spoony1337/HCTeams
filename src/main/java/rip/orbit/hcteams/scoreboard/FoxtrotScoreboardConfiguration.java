package rip.orbit.hcteams.scoreboard;

import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.TitleGetter;
import rip.orbit.hcteams.HCF;

public class FoxtrotScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitleGetter(new TitleGetter(HCF.getInstance().getMapHandler().getScoreboardTitle()));
        configuration.setScoreGetter(new FoxtrotScoreGetter());

        return (configuration);
    }

}