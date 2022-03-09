package rip.orbit.hcteams.persist.maps;

import rip.orbit.hcteams.persist.PersistMap;

import java.util.UUID;

public class LivesMap extends PersistMap<Integer> {

    public LivesMap() {
        super("Lives", "Lives");
    }


    @Override
	public String getRedisValue(Integer lives) {
        return (String.valueOf(lives));
    }

    
    @Override
	public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    
    @Override
	public Object getMongoValue(Integer lives) {
        return (lives);
    }

    public int getLives(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setLives(UUID update, int lives) {
        updateValueSync(update, lives);
    }

    public void addLives(UUID update, int amount) {
        setLives(update, getLives(update) + amount);
    }

}