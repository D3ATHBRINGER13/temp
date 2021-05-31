package net.minecraft.client.renderer.entity;

import java.util.function.Consumer;
import net.minecraft.Util;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.packs.resources.Resource;
import java.io.IOException;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.util.Mth;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.world.entity.npc.VillagerType;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class VillagerProfessionLayer<T extends LivingEntity, M extends EntityModel> extends RenderLayer<T, M> implements ResourceManagerReloadListener {
    private static final Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS;
    private final Object2ObjectMap<VillagerType, VillagerMetaDataSection.Hat> typeHatCache;
    private final Object2ObjectMap<VillagerProfession, VillagerMetaDataSection.Hat> professionHatCache;
    private final ReloadableResourceManager resourceManager;
    private final String path;
    
    public VillagerProfessionLayer(final RenderLayerParent<T, M> dtr, final ReloadableResourceManager xg, final String string) {
        super(dtr);
        this.typeHatCache = (Object2ObjectMap<VillagerType, VillagerMetaDataSection.Hat>)new Object2ObjectOpenHashMap();
        this.professionHatCache = (Object2ObjectMap<VillagerProfession, VillagerMetaDataSection.Hat>)new Object2ObjectOpenHashMap();
        this.resourceManager = xg;
        this.path = string;
        xg.registerReloadListener(this);
    }
    
    @Override
    public void render(final T aix, final float float2, final float float3, final float float4, final float float5, final float float6, final float float7, final float float8) {
        if (((Entity)aix).isInvisible()) {
            return;
        }
        final VillagerData avu10 = ((VillagerDataHolder)aix).getVillagerData();
        final VillagerType avy11 = avu10.getType();
        final VillagerProfession avw12 = avu10.getProfession();
        final VillagerMetaDataSection.Hat a13 = this.<VillagerType>getHatData(this.typeHatCache, "type", Registry.VILLAGER_TYPE, avy11);
        final VillagerMetaDataSection.Hat a14 = this.<VillagerProfession>getHatData(this.professionHatCache, "profession", Registry.VILLAGER_PROFESSION, avw12);
        final M dhh15 = this.getParentModel();
        this.bindTexture(this.getResourceLocation("type", Registry.VILLAGER_TYPE.getKey(avy11)));
        ((VillagerHeadModel)dhh15).hatVisible(a14 == VillagerMetaDataSection.Hat.NONE || (a14 == VillagerMetaDataSection.Hat.PARTIAL && a13 != VillagerMetaDataSection.Hat.FULL));
        ((net.minecraft.client.model.EntityModel<T>)dhh15).render(aix, float2, float3, float5, float6, float7, float8);
        ((VillagerHeadModel)dhh15).hatVisible(true);
        if (avw12 != VillagerProfession.NONE && !((LivingEntity)aix).isBaby()) {
            this.bindTexture(this.getResourceLocation("profession", Registry.VILLAGER_PROFESSION.getKey(avw12)));
            ((net.minecraft.client.model.EntityModel<T>)dhh15).render(aix, float2, float3, float5, float6, float7, float8);
            this.bindTexture(this.getResourceLocation("profession_level", (ResourceLocation)VillagerProfessionLayer.LEVEL_LOCATIONS.get(Mth.clamp(avu10.getLevel(), 1, VillagerProfessionLayer.LEVEL_LOCATIONS.size()))));
            ((net.minecraft.client.model.EntityModel<T>)dhh15).render(aix, float2, float3, float5, float6, float7, float8);
        }
    }
    
    @Override
    public boolean colorsOnDamage() {
        return true;
    }
    
    private ResourceLocation getResourceLocation(final String string, final ResourceLocation qv) {
        return new ResourceLocation(qv.getNamespace(), "textures/entity/" + this.path + "/" + string + "/" + qv.getPath() + ".png");
    }
    
    public <K> VillagerMetaDataSection.Hat getHatData(final Object2ObjectMap<K, VillagerMetaDataSection.Hat> object2ObjectMap, final String string, final DefaultedRegistry<K> fa, final K object) {
        return (VillagerMetaDataSection.Hat)object2ObjectMap.computeIfAbsent(object, object4 -> {
            try (final Resource xh6 = this.resourceManager.getResource(this.getResourceLocation(string, fa.getKey(object)))) {
                final VillagerMetaDataSection dyf8 = xh6.<VillagerMetaDataSection>getMetadata((MetadataSectionSerializer<VillagerMetaDataSection>)VillagerMetaDataSection.SERIALIZER);
                if (dyf8 != null) {
                    return dyf8.getHat();
                }
            }
            catch (IOException ex) {}
            return VillagerMetaDataSection.Hat.NONE;
        });
    }
    
    @Override
    public void onResourceManagerReload(final ResourceManager xi) {
        this.professionHatCache.clear();
        this.typeHatCache.clear();
    }
    
    static {
        LEVEL_LOCATIONS = Util.<Int2ObjectMap>make((Int2ObjectMap)new Int2ObjectOpenHashMap(), (java.util.function.Consumer<Int2ObjectMap>)(int2ObjectOpenHashMap -> {
            int2ObjectOpenHashMap.put(1, new ResourceLocation("stone"));
            int2ObjectOpenHashMap.put(2, new ResourceLocation("iron"));
            int2ObjectOpenHashMap.put(3, new ResourceLocation("gold"));
            int2ObjectOpenHashMap.put(4, new ResourceLocation("emerald"));
            int2ObjectOpenHashMap.put(5, new ResourceLocation("diamond"));
        }));
    }
}
