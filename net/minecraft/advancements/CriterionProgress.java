package net.minecraft.advancements;

import java.text.ParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import java.util.Date;
import java.text.SimpleDateFormat;

public class CriterionProgress {
    private static final SimpleDateFormat DATE_FORMAT;
    private Date obtained;
    
    public boolean isDone() {
        return this.obtained != null;
    }
    
    public void grant() {
        this.obtained = new Date();
    }
    
    public void revoke() {
        this.obtained = null;
    }
    
    public Date getObtained() {
        return this.obtained;
    }
    
    public String toString() {
        return new StringBuilder().append("CriterionProgress{obtained=").append((this.obtained == null) ? "false" : this.obtained).append('}').toString();
    }
    
    public void serializeToNetwork(final FriendlyByteBuf je) {
        je.writeBoolean(this.obtained != null);
        if (this.obtained != null) {
            je.writeDate(this.obtained);
        }
    }
    
    public JsonElement serializeToJson() {
        if (this.obtained != null) {
            return (JsonElement)new JsonPrimitive(CriterionProgress.DATE_FORMAT.format(this.obtained));
        }
        return (JsonElement)JsonNull.INSTANCE;
    }
    
    public static CriterionProgress fromNetwork(final FriendlyByteBuf je) {
        final CriterionProgress w2 = new CriterionProgress();
        if (je.readBoolean()) {
            w2.obtained = je.readDate();
        }
        return w2;
    }
    
    public static CriterionProgress fromJson(final String string) {
        final CriterionProgress w2 = new CriterionProgress();
        try {
            w2.obtained = CriterionProgress.DATE_FORMAT.parse(string);
        }
        catch (ParseException parseException3) {
            throw new JsonSyntaxException("Invalid datetime: " + string, (Throwable)parseException3);
        }
        return w2;
    }
    
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    }
}
