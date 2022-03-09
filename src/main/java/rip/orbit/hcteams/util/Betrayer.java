package rip.orbit.hcteams.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Betrayer {

    private UUID uuid;
    private UUID addedBy;
    private String reason;
    private long time;

    public Betrayer(UUID uuid, UUID addedBy, String reason) {
        this(uuid, addedBy, reason, System.currentTimeMillis());
    }

}
