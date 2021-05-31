package com.mojang.realmsclient.dto;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;

public abstract class ValueObject {
    public String toString() {
        final StringBuilder stringBuilder2 = new StringBuilder("{");
        for (final Field field6 : this.getClass().getFields()) {
            if (!isStatic(field6)) {
                try {
                    stringBuilder2.append(field6.getName()).append("=").append(field6.get(this)).append(" ");
                }
                catch (IllegalAccessException illegalAccessException7) {}
            }
        }
        stringBuilder2.deleteCharAt(stringBuilder2.length() - 1);
        stringBuilder2.append('}');
        return stringBuilder2.toString();
    }
    
    private static boolean isStatic(final Field field) {
        return Modifier.isStatic(field.getModifiers());
    }
}
