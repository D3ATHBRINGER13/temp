package com.mojang.realmsclient.dto;

import java.util.Iterator;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.HashSet;
import java.util.Set;

public class Ops extends ValueObject {
    public Set<String> ops;
    
    public Ops() {
        this.ops = (Set<String>)new HashSet();
    }
    
    public static Ops parse(final String string) {
        final Ops ops2 = new Ops();
        final JsonParser jsonParser3 = new JsonParser();
        try {
            final JsonElement jsonElement4 = jsonParser3.parse(string);
            final JsonObject jsonObject5 = jsonElement4.getAsJsonObject();
            final JsonElement jsonElement5 = jsonObject5.get("ops");
            if (jsonElement5.isJsonArray()) {
                for (final JsonElement jsonElement6 : jsonElement5.getAsJsonArray()) {
                    ops2.ops.add(jsonElement6.getAsString());
                }
            }
        }
        catch (Exception ex) {}
        return ops2;
    }
}
