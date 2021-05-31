package net.minecraft.server.players;

import java.text.ParseException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.common.collect.Iterators;
import java.io.BufferedWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;
import java.io.BufferedReader;
import org.apache.commons.io.IOUtils;
import com.google.gson.JsonParseException;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.io.Reader;
import net.minecraft.util.GsonHelper;
import java.util.List;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Calendar;
import java.util.Date;
import net.minecraft.world.entity.player.Player;
import com.mojang.authlib.Agent;
import com.mojang.authlib.ProfileLookupCallback;
import com.google.gson.GsonBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.ParameterizedType;
import java.io.File;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.GameProfile;
import java.util.Deque;
import java.util.UUID;
import java.util.Map;
import java.text.SimpleDateFormat;

public class GameProfileCache {
    public static final SimpleDateFormat DATE_FORMAT;
    private static boolean usesAuthentication;
    private final Map<String, GameProfileInfo> profilesByName;
    private final Map<UUID, GameProfileInfo> profilesByUUID;
    private final Deque<GameProfile> profileMRUList;
    private final GameProfileRepository profileRepository;
    protected final Gson gson;
    private final File file;
    private static final ParameterizedType GAMEPROFILE_ENTRY_TYPE;
    
    public GameProfileCache(final GameProfileRepository gameProfileRepository, final File file) {
        this.profilesByName = (Map<String, GameProfileInfo>)Maps.newHashMap();
        this.profilesByUUID = (Map<UUID, GameProfileInfo>)Maps.newHashMap();
        this.profileMRUList = (Deque<GameProfile>)Lists.newLinkedList();
        this.profileRepository = gameProfileRepository;
        this.file = file;
        final GsonBuilder gsonBuilder4 = new GsonBuilder();
        gsonBuilder4.registerTypeHierarchyAdapter((Class)GameProfileInfo.class, new Serializer());
        this.gson = gsonBuilder4.create();
        this.load();
    }
    
    private static GameProfile lookupGameProfile(final GameProfileRepository gameProfileRepository, final String string) {
        final GameProfile[] arr3 = { null };
        final ProfileLookupCallback profileLookupCallback4 = (ProfileLookupCallback)new ProfileLookupCallback() {
            public void onProfileLookupSucceeded(final GameProfile gameProfile) {
                arr3[0] = gameProfile;
            }
            
            public void onProfileLookupFailed(final GameProfile gameProfile, final Exception exception) {
                arr3[0] = null;
            }
        };
        gameProfileRepository.findProfilesByNames(new String[] { string }, Agent.MINECRAFT, profileLookupCallback4);
        if (!usesAuthentication() && arr3[0] == null) {
            final UUID uUID5 = Player.createPlayerUUID(new GameProfile((UUID)null, string));
            final GameProfile gameProfile6 = new GameProfile(uUID5, string);
            profileLookupCallback4.onProfileLookupSucceeded(gameProfile6);
        }
        return arr3[0];
    }
    
    public static void setUsesAuthentication(final boolean boolean1) {
        GameProfileCache.usesAuthentication = boolean1;
    }
    
    private static boolean usesAuthentication() {
        return GameProfileCache.usesAuthentication;
    }
    
    public void add(final GameProfile gameProfile) {
        this.add(gameProfile, null);
    }
    
    private void add(final GameProfile gameProfile, Date date) {
        final UUID uUID4 = gameProfile.getId();
        if (date == null) {
            final Calendar calendar5 = Calendar.getInstance();
            calendar5.setTime(new Date());
            calendar5.add(2, 1);
            date = calendar5.getTime();
        }
        final GameProfileInfo a5 = new GameProfileInfo(gameProfile, date);
        if (this.profilesByUUID.containsKey(uUID4)) {
            final GameProfileInfo a6 = (GameProfileInfo)this.profilesByUUID.get(uUID4);
            this.profilesByName.remove(a6.getProfile().getName().toLowerCase(Locale.ROOT));
            this.profileMRUList.remove(gameProfile);
        }
        this.profilesByName.put(gameProfile.getName().toLowerCase(Locale.ROOT), a5);
        this.profilesByUUID.put(uUID4, a5);
        this.profileMRUList.addFirst(gameProfile);
        this.save();
    }
    
    @Nullable
    public GameProfile get(final String string) {
        final String string2 = string.toLowerCase(Locale.ROOT);
        GameProfileInfo a4 = (GameProfileInfo)this.profilesByName.get(string2);
        if (a4 != null && new Date().getTime() >= a4.expirationDate.getTime()) {
            this.profilesByUUID.remove(a4.getProfile().getId());
            this.profilesByName.remove(a4.getProfile().getName().toLowerCase(Locale.ROOT));
            this.profileMRUList.remove(a4.getProfile());
            a4 = null;
        }
        if (a4 != null) {
            final GameProfile gameProfile5 = a4.getProfile();
            this.profileMRUList.remove(gameProfile5);
            this.profileMRUList.addFirst(gameProfile5);
        }
        else {
            final GameProfile gameProfile5 = lookupGameProfile(this.profileRepository, string2);
            if (gameProfile5 != null) {
                this.add(gameProfile5);
                a4 = (GameProfileInfo)this.profilesByName.get(string2);
            }
        }
        this.save();
        return (a4 == null) ? null : a4.getProfile();
    }
    
    @Nullable
    public GameProfile get(final UUID uUID) {
        final GameProfileInfo a3 = (GameProfileInfo)this.profilesByUUID.get(uUID);
        return (a3 == null) ? null : a3.getProfile();
    }
    
    private GameProfileInfo getProfileInfo(final UUID uUID) {
        final GameProfileInfo a3 = (GameProfileInfo)this.profilesByUUID.get(uUID);
        if (a3 != null) {
            final GameProfile gameProfile4 = a3.getProfile();
            this.profileMRUList.remove(gameProfile4);
            this.profileMRUList.addFirst(gameProfile4);
        }
        return a3;
    }
    
    public void load() {
        BufferedReader bufferedReader2 = null;
        try {
            bufferedReader2 = Files.newReader(this.file, StandardCharsets.UTF_8);
            final List<GameProfileInfo> list3 = GsonHelper.fromJson(this.gson, (Reader)bufferedReader2, (Type)GameProfileCache.GAMEPROFILE_ENTRY_TYPE);
            this.profilesByName.clear();
            this.profilesByUUID.clear();
            this.profileMRUList.clear();
            if (list3 != null) {
                for (final GameProfileInfo a5 : Lists.reverse((List)list3)) {
                    if (a5 != null) {
                        this.add(a5.getProfile(), a5.getExpirationDate());
                    }
                }
            }
        }
        catch (FileNotFoundException ex) {}
        catch (JsonParseException ex2) {}
        finally {
            IOUtils.closeQuietly((Reader)bufferedReader2);
        }
    }
    
    public void save() {
        final String string2 = this.gson.toJson(this.getTopMRUProfiles(1000));
        BufferedWriter bufferedWriter3 = null;
        try {
            bufferedWriter3 = Files.newWriter(this.file, StandardCharsets.UTF_8);
            bufferedWriter3.write(string2);
        }
        catch (FileNotFoundException fileNotFoundException4) {}
        catch (IOException iOException4) {}
        finally {
            IOUtils.closeQuietly((Writer)bufferedWriter3);
        }
    }
    
    private List<GameProfileInfo> getTopMRUProfiles(final int integer) {
        final List<GameProfileInfo> list3 = (List<GameProfileInfo>)Lists.newArrayList();
        final List<GameProfile> list4 = (List<GameProfile>)Lists.newArrayList(Iterators.limit(this.profileMRUList.iterator(), integer));
        for (final GameProfile gameProfile6 : list4) {
            final GameProfileInfo a7 = this.getProfileInfo(gameProfile6.getId());
            if (a7 == null) {
                continue;
            }
            list3.add(a7);
        }
        return list3;
    }
    
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        GAMEPROFILE_ENTRY_TYPE = (ParameterizedType)new ParameterizedType() {
            public Type[] getActualTypeArguments() {
                return new Type[] { (Type)GameProfileInfo.class };
            }
            
            public Type getRawType() {
                return (Type)List.class;
            }
            
            public Type getOwnerType() {
                return null;
            }
        };
    }
    
    class Serializer implements JsonDeserializer<GameProfileInfo>, JsonSerializer<GameProfileInfo> {
        private Serializer() {
        }
        
        public JsonElement serialize(final GameProfileInfo a, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject5 = new JsonObject();
            jsonObject5.addProperty("name", a.getProfile().getName());
            final UUID uUID6 = a.getProfile().getId();
            jsonObject5.addProperty("uuid", (uUID6 == null) ? "" : uUID6.toString());
            jsonObject5.addProperty("expiresOn", GameProfileCache.DATE_FORMAT.format(a.getExpirationDate()));
            return (JsonElement)jsonObject5;
        }
        
        public GameProfileInfo deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) {
                return null;
            }
            final JsonObject jsonObject5 = jsonElement.getAsJsonObject();
            final JsonElement jsonElement2 = jsonObject5.get("name");
            final JsonElement jsonElement3 = jsonObject5.get("uuid");
            final JsonElement jsonElement4 = jsonObject5.get("expiresOn");
            if (jsonElement2 == null || jsonElement3 == null) {
                return null;
            }
            final String string9 = jsonElement3.getAsString();
            final String string10 = jsonElement2.getAsString();
            Date date11 = null;
            if (jsonElement4 != null) {
                try {
                    date11 = GameProfileCache.DATE_FORMAT.parse(jsonElement4.getAsString());
                }
                catch (ParseException parseException12) {
                    date11 = null;
                }
            }
            if (string10 == null || string9 == null) {
                return null;
            }
            UUID uUID12;
            try {
                uUID12 = UUID.fromString(string9);
            }
            catch (Throwable throwable13) {
                return null;
            }
            return new GameProfileInfo(new GameProfile(uUID12, string10), date11);
        }
    }
    
    class GameProfileInfo {
        private final GameProfile profile;
        private final Date expirationDate;
        
        private GameProfileInfo(final GameProfile gameProfile, final Date date) {
            this.profile = gameProfile;
            this.expirationDate = date;
        }
        
        public GameProfile getProfile() {
            return this.profile;
        }
        
        public Date getExpirationDate() {
            return this.expirationDate;
        }
    }
}
