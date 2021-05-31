package net.minecraft.client.model;

import java.util.Random;
import com.google.common.collect.Lists;
import net.minecraft.client.model.geom.ModelPart;
import java.util.List;

public class Model {
    public final List<ModelPart> cubes;
    public int texWidth;
    public int texHeight;
    
    public Model() {
        this.cubes = (List<ModelPart>)Lists.newArrayList();
        this.texWidth = 64;
        this.texHeight = 32;
    }
    
    public ModelPart getRandomModelPart(final Random random) {
        return (ModelPart)this.cubes.get(random.nextInt(this.cubes.size()));
    }
}
