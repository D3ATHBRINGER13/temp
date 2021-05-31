package net.minecraft.world.level.levelgen.structure.templatesystem;

import org.apache.logging.log4j.LogManager;
import java.nio.file.InvalidPathException;
import net.minecraft.ResourceLocationException;
import net.minecraft.FileUtil;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.nbt.NbtIo;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import net.minecraft.server.packs.resources.Resource;
import java.io.FileNotFoundException;
import net.minecraft.server.packs.resources.ResourceManager;
import javax.annotation.Nullable;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import com.google.common.collect.Maps;
import java.io.File;
import java.nio.file.Path;
import net.minecraft.server.MinecraftServer;
import com.mojang.datafixers.DataFixer;
import net.minecraft.resources.ResourceLocation;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class StructureManager implements ResourceManagerReloadListener {
    private static final Logger LOGGER;
    private final Map<ResourceLocation, StructureTemplate> structureRepository;
    private final DataFixer fixerUpper;
    private final MinecraftServer server;
    private final Path generatedDir;
    
    public StructureManager(final MinecraftServer minecraftServer, final File file, final DataFixer dataFixer) {
        this.structureRepository = (Map<ResourceLocation, StructureTemplate>)Maps.newHashMap();
        this.server = minecraftServer;
        this.fixerUpper = dataFixer;
        this.generatedDir = file.toPath().resolve("generated").normalize();
        minecraftServer.getResources().registerReloadListener(this);
    }
    
    public StructureTemplate getOrCreate(final ResourceLocation qv) {
        StructureTemplate cjt3 = this.get(qv);
        if (cjt3 == null) {
            cjt3 = new StructureTemplate();
            this.structureRepository.put(qv, cjt3);
        }
        return cjt3;
    }
    
    @Nullable
    public StructureTemplate get(final ResourceLocation qv) {
        return (StructureTemplate)this.structureRepository.computeIfAbsent(qv, qv -> {
            final StructureTemplate cjt3 = this.loadFromGenerated(qv);
            return (cjt3 != null) ? cjt3 : this.loadFromResource(qv);
        });
    }
    
    public void onResourceManagerReload(final ResourceManager xi) {
        this.structureRepository.clear();
    }
    
    @Nullable
    private StructureTemplate loadFromResource(final ResourceLocation qv) {
        final ResourceLocation qv2 = new ResourceLocation(qv.getNamespace(), "structures/" + qv.getPath() + ".nbt");
        try (final Resource xh4 = this.server.getResources().getResource(qv2)) {
            return this.readStructure(xh4.getInputStream());
        }
        catch (FileNotFoundException fileNotFoundException4) {
            return null;
        }
        catch (Throwable throwable4) {
            StructureManager.LOGGER.error("Couldn't load structure {}: {}", qv, throwable4.toString());
            return null;
        }
    }
    
    @Nullable
    private StructureTemplate loadFromGenerated(final ResourceLocation qv) {
        if (!this.generatedDir.toFile().isDirectory()) {
            return null;
        }
        final Path path3 = this.createAndValidatePathToStructure(qv, ".nbt");
        try (final InputStream inputStream4 = (InputStream)new FileInputStream(path3.toFile())) {
            return this.readStructure(inputStream4);
        }
        catch (FileNotFoundException fileNotFoundException4) {
            return null;
        }
        catch (IOException iOException4) {
            StructureManager.LOGGER.error("Couldn't load structure from {}", path3, iOException4);
            return null;
        }
    }
    
    private StructureTemplate readStructure(final InputStream inputStream) throws IOException {
        final CompoundTag id3 = NbtIo.readCompressed(inputStream);
        if (!id3.contains("DataVersion", 99)) {
            id3.putInt("DataVersion", 500);
        }
        final StructureTemplate cjt4 = new StructureTemplate();
        cjt4.load(NbtUtils.update(this.fixerUpper, DataFixTypes.STRUCTURE, id3, id3.getInt("DataVersion")));
        return cjt4;
    }
    
    public boolean save(final ResourceLocation qv) {
        final StructureTemplate cjt3 = (StructureTemplate)this.structureRepository.get(qv);
        if (cjt3 == null) {
            return false;
        }
        final Path path4 = this.createAndValidatePathToStructure(qv, ".nbt");
        final Path path5 = path4.getParent();
        if (path5 == null) {
            return false;
        }
        try {
            Files.createDirectories(Files.exists(path5, new LinkOption[0]) ? path5.toRealPath(new LinkOption[0]) : path5, new FileAttribute[0]);
        }
        catch (IOException iOException6) {
            StructureManager.LOGGER.error("Failed to create parent directory: {}", path5);
            return false;
        }
        final CompoundTag id6 = cjt3.save(new CompoundTag());
        try (final OutputStream outputStream7 = (OutputStream)new FileOutputStream(path4.toFile())) {
            NbtIo.writeCompressed(id6, outputStream7);
        }
        catch (Throwable throwable7) {
            return false;
        }
        return true;
    }
    
    private Path createPathToStructure(final ResourceLocation qv, final String string) {
        try {
            final Path path4 = this.generatedDir.resolve(qv.getNamespace());
            final Path path5 = path4.resolve("structures");
            return FileUtil.createPathToResource(path5, qv.getPath(), string);
        }
        catch (InvalidPathException invalidPathException4) {
            throw new ResourceLocationException(new StringBuilder().append("Invalid resource path: ").append(qv).toString(), (Throwable)invalidPathException4);
        }
    }
    
    private Path createAndValidatePathToStructure(final ResourceLocation qv, final String string) {
        if (qv.getPath().contains("//")) {
            throw new ResourceLocationException(new StringBuilder().append("Invalid resource path: ").append(qv).toString());
        }
        final Path path4 = this.createPathToStructure(qv, string);
        if (!path4.startsWith(this.generatedDir) || !FileUtil.isPathNormalized(path4) || !FileUtil.isPathPortable(path4)) {
            throw new ResourceLocationException(new StringBuilder().append("Invalid resource path: ").append(path4).toString());
        }
        return path4;
    }
    
    public void remove(final ResourceLocation qv) {
        this.structureRepository.remove(qv);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
