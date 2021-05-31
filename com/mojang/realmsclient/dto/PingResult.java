package com.mojang.realmsclient.dto;

import java.util.ArrayList;
import java.util.List;

public class PingResult extends ValueObject {
    public List<RegionPingResult> pingResults;
    public List<Long> worldIds;
    
    public PingResult() {
        this.pingResults = (List<RegionPingResult>)new ArrayList();
        this.worldIds = (List<Long>)new ArrayList();
    }
}
