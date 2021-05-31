package net.minecraft.server.players;

import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import org.apache.logging.log4j.LogManager;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.io.Reader;
import net.minecraft.util.GsonHelper;
import java.io.BufferedWriter;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import com.google.common.io.Files;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.io.IOException;
import com.google.gson.GsonBuilder;
import com.google.common.collect.Maps;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.io.File;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;

public class StoredUserList<K, V extends StoredUserEntry<K>> {
    protected static final Logger LOGGER;
    protected final Gson gson;
    private final File file;
    private final Map<String, V> map;
    private boolean enabled;
    private static final ParameterizedType USERLIST_ENTRY_TYPE;
    
    public StoredUserList(final File file) {
        this.map = (Map<String, V>)Maps.newHashMap();
        this.enabled = true;
        this.file = file;
        final GsonBuilder gsonBuilder3 = new GsonBuilder().setPrettyPrinting();
        gsonBuilder3.registerTypeHierarchyAdapter((Class)StoredUserEntry.class, new Serializer());
        this.gson = gsonBuilder3.create();
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean boolean1) {
        this.enabled = boolean1;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void add(final V xy) {
        this.map.put(this.getKeyForUser(xy.getUser()), xy);
        try {
            this.save();
        }
        catch (IOException iOException3) {
            StoredUserList.LOGGER.warn("Could not save the list after adding a user.", (Throwable)iOException3);
        }
    }
    
    @Nullable
    public V get(final K object) {
        this.removeExpired();
        return (V)this.map.get(this.getKeyForUser(object));
    }
    
    public void remove(final K object) {
        this.map.remove(this.getKeyForUser(object));
        try {
            this.save();
        }
        catch (IOException iOException3) {
            StoredUserList.LOGGER.warn("Could not save the list after removing a user.", (Throwable)iOException3);
        }
    }
    
    public void remove(final StoredUserEntry<K> xy) {
        this.remove(xy.getUser());
    }
    
    public String[] getUserList() {
        return (String[])this.map.keySet().toArray((Object[])new String[this.map.size()]);
    }
    
    public boolean isEmpty() {
        return this.map.size() < 1;
    }
    
    protected String getKeyForUser(final K object) {
        return object.toString();
    }
    
    protected boolean contains(final K object) {
        return this.map.containsKey(this.getKeyForUser(object));
    }
    
    private void removeExpired() {
        final List<K> list2 = (List<K>)Lists.newArrayList();
        for (final V xy4 : this.map.values()) {
            if (xy4.hasExpired()) {
                list2.add(((StoredUserEntry<Object>)xy4).getUser());
            }
        }
        for (final K object4 : list2) {
            this.map.remove(this.getKeyForUser(object4));
        }
    }
    
    protected StoredUserEntry<K> createEntry(final JsonObject jsonObject) {
        return new StoredUserEntry<K>(null, jsonObject);
    }
    
    public Collection<V> getEntries() {
        return (Collection<V>)this.map.values();
    }
    
    public void save() throws IOException {
        final Collection<V> collection2 = (Collection<V>)this.map.values();
        final String string3 = this.gson.toJson(collection2);
        BufferedWriter bufferedWriter4 = null;
        try {
            bufferedWriter4 = Files.newWriter(this.file, StandardCharsets.UTF_8);
            bufferedWriter4.write(string3);
        }
        finally {
            IOUtils.closeQuietly((Writer)bufferedWriter4);
        }
    }
    
    public void load() throws FileNotFoundException {
        if (!this.file.exists()) {
            return;
        }
        BufferedReader bufferedReader2 = null;
        try {
            bufferedReader2 = Files.newReader(this.file, StandardCharsets.UTF_8);
            final Collection<StoredUserEntry<K>> collection3 = GsonHelper.fromJson(this.gson, (Reader)bufferedReader2, (Type)StoredUserList.USERLIST_ENTRY_TYPE);
            if (collection3 != null) {
                this.map.clear();
                for (final StoredUserEntry<K> xy5 : collection3) {
                    if (xy5.getUser() != null) {
                        this.map.put(this.getKeyForUser(xy5.getUser()), xy5);
                    }
                }
            }
        }
        finally {
            IOUtils.closeQuietly((Reader)bufferedReader2);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        USERLIST_ENTRY_TYPE = (ParameterizedType)new ParameterizedType() {
            public Type[] getActualTypeArguments() {
                return new Type[] { (Type)StoredUserEntry.class };
            }
            
            public Type getRawType() {
                return (Type)List.class;
            }
            
            public Type getOwnerType() {
                return null;
            }
        };
    }
    
    class Serializer implements JsonDeserializer<StoredUserEntry<K>>, JsonSerializer<StoredUserEntry<K>> {
        private Serializer() {
        }
        
        public JsonElement serialize(final StoredUserEntry<K> xy, final Type type, final JsonSerializationContext jsonSerializationContext) {
            final JsonObject jsonObject5 = new JsonObject();
            xy.serialize(jsonObject5);
            return (JsonElement)jsonObject5;
        }
        
        public StoredUserEntry<K> deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                final JsonObject jsonObject5 = jsonElement.getAsJsonObject();
                return StoredUserList.this.createEntry(jsonObject5);
            }
            return null;
        }
    }
}
