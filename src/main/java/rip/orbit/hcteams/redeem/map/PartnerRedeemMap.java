package rip.orbit.hcteams.redeem.map;

import rip.orbit.hcteams.persist.PersistMap;

import java.util.UUID;

public class PartnerRedeemMap extends PersistMap<Boolean> {

    public PartnerRedeemMap() {
        super("PartnerRedeems", "PartnerRedeemed");
    }

    
    @Override
	public String getRedisValue(Boolean toggled){
        return (String.valueOf(toggled));
    }

    
    @Override
	public Boolean getJavaObject(String str){
        return (Boolean.valueOf(str));
    }

    
    @Override
	public Object getMongoValue(Boolean toggled) {
        return (toggled);
    }

    public void setToggled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isToggled(UUID check) {
        return (contains(check) ? getValue(check) : false);
    }

}