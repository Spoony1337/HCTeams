package rip.orbit.hcteams.persist.maps;

import rip.orbit.hcteams.persist.PersistMap;

import java.util.UUID;

public class RedstoneMinedMap extends PersistMap<Integer> {

    public RedstoneMinedMap() {
        super("RedstoneMined", "MiningStats.Redstone");
    }

    
    @Override
	public String getRedisValue(Integer kills) {
        return (String.valueOf(kills));
    }

    
    @Override
	public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    
    @Override
	public Object getMongoValue(Integer mined) {
        return (mined);
    }

    public int getMined(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setMined(UUID update, int mined) {
        updateValueAsync(update, mined);
    }

}