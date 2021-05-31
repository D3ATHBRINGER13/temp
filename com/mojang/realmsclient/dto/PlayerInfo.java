package com.mojang.realmsclient.dto;

public class PlayerInfo extends ValueObject {
    private String name;
    private String uuid;
    private boolean operator;
    private boolean accepted;
    private boolean online;
    
    public PlayerInfo() {
        this.operator = false;
        this.accepted = false;
        this.online = false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String string) {
        this.name = string;
    }
    
    public String getUuid() {
        return this.uuid;
    }
    
    public void setUuid(final String string) {
        this.uuid = string;
    }
    
    public boolean isOperator() {
        return this.operator;
    }
    
    public void setOperator(final boolean boolean1) {
        this.operator = boolean1;
    }
    
    public boolean getAccepted() {
        return this.accepted;
    }
    
    public void setAccepted(final boolean boolean1) {
        this.accepted = boolean1;
    }
    
    public boolean getOnline() {
        return this.online;
    }
    
    public void setOnline(final boolean boolean1) {
        this.online = boolean1;
    }
}
