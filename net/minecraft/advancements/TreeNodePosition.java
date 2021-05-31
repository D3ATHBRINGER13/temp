package net.minecraft.advancements;

import java.util.Iterator;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import java.util.List;

public class TreeNodePosition {
    private final Advancement advancement;
    private final TreeNodePosition parent;
    private final TreeNodePosition previousSibling;
    private final int childIndex;
    private final List<TreeNodePosition> children;
    private TreeNodePosition ancestor;
    private TreeNodePosition thread;
    private int x;
    private float y;
    private float mod;
    private float change;
    private float shift;
    
    public TreeNodePosition(final Advancement q, @Nullable final TreeNodePosition ac2, @Nullable final TreeNodePosition ac3, final int integer4, final int integer5) {
        this.children = (List<TreeNodePosition>)Lists.newArrayList();
        if (q.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position an invisible advancement!");
        }
        this.advancement = q;
        this.parent = ac2;
        this.previousSibling = ac3;
        this.childIndex = integer4;
        this.ancestor = this;
        this.x = integer5;
        this.y = -1.0f;
        TreeNodePosition ac4 = null;
        for (final Advancement q2 : q.getChildren()) {
            ac4 = this.addChild(q2, ac4);
        }
    }
    
    @Nullable
    private TreeNodePosition addChild(final Advancement q, @Nullable TreeNodePosition ac) {
        if (q.getDisplay() != null) {
            ac = new TreeNodePosition(q, this, ac, this.children.size() + 1, this.x + 1);
            this.children.add(ac);
        }
        else {
            for (final Advancement q2 : q.getChildren()) {
                ac = this.addChild(q2, ac);
            }
        }
        return ac;
    }
    
    private void firstWalk() {
        if (this.children.isEmpty()) {
            if (this.previousSibling != null) {
                this.y = this.previousSibling.y + 1.0f;
            }
            else {
                this.y = 0.0f;
            }
            return;
        }
        TreeNodePosition ac2 = null;
        for (final TreeNodePosition ac3 : this.children) {
            ac3.firstWalk();
            ac2 = ac3.apportion((ac2 == null) ? ac3 : ac2);
        }
        this.executeShifts();
        final float float3 = (((TreeNodePosition)this.children.get(0)).y + ((TreeNodePosition)this.children.get(this.children.size() - 1)).y) / 2.0f;
        if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0f;
            this.mod = this.y - float3;
        }
        else {
            this.y = float3;
        }
    }
    
    private float secondWalk(final float float1, final int integer, float float3) {
        this.y += float1;
        this.x = integer;
        if (this.y < float3) {
            float3 = this.y;
        }
        for (final TreeNodePosition ac6 : this.children) {
            float3 = ac6.secondWalk(float1 + this.mod, integer + 1, float3);
        }
        return float3;
    }
    
    private void thirdWalk(final float float1) {
        this.y += float1;
        for (final TreeNodePosition ac4 : this.children) {
            ac4.thirdWalk(float1);
        }
    }
    
    private void executeShifts() {
        float float2 = 0.0f;
        float float3 = 0.0f;
        for (int integer4 = this.children.size() - 1; integer4 >= 0; --integer4) {
            final TreeNodePosition treeNodePosition;
            final TreeNodePosition ac5 = treeNodePosition = (TreeNodePosition)this.children.get(integer4);
            treeNodePosition.y += float2;
            final TreeNodePosition treeNodePosition2 = ac5;
            treeNodePosition2.mod += float2;
            float3 += ac5.change;
            float2 += ac5.shift + float3;
        }
    }
    
    @Nullable
    private TreeNodePosition previousOrThread() {
        if (this.thread != null) {
            return this.thread;
        }
        if (!this.children.isEmpty()) {
            return (TreeNodePosition)this.children.get(0);
        }
        return null;
    }
    
    @Nullable
    private TreeNodePosition nextOrThread() {
        if (this.thread != null) {
            return this.thread;
        }
        if (!this.children.isEmpty()) {
            return (TreeNodePosition)this.children.get(this.children.size() - 1);
        }
        return null;
    }
    
    private TreeNodePosition apportion(TreeNodePosition ac) {
        if (this.previousSibling == null) {
            return ac;
        }
        TreeNodePosition ac2 = this;
        TreeNodePosition ac3 = this;
        TreeNodePosition ac4 = this.previousSibling;
        TreeNodePosition ac5 = (TreeNodePosition)this.parent.children.get(0);
        float float7 = this.mod;
        float float8 = this.mod;
        float float9 = ac4.mod;
        float float10 = ac5.mod;
        while (ac4.nextOrThread() != null && ac2.previousOrThread() != null) {
            ac4 = ac4.nextOrThread();
            ac2 = ac2.previousOrThread();
            ac5 = ac5.previousOrThread();
            ac3 = ac3.nextOrThread();
            ac3.ancestor = this;
            final float float11 = ac4.y + float9 - (ac2.y + float7) + 1.0f;
            if (float11 > 0.0f) {
                ac4.getAncestor(this, ac).moveSubtree(this, float11);
                float7 += float11;
                float8 += float11;
            }
            float9 += ac4.mod;
            float7 += ac2.mod;
            float10 += ac5.mod;
            float8 += ac3.mod;
        }
        if (ac4.nextOrThread() != null && ac3.nextOrThread() == null) {
            ac3.thread = ac4.nextOrThread();
            final TreeNodePosition treeNodePosition = ac3;
            treeNodePosition.mod += float9 - float8;
        }
        else {
            if (ac2.previousOrThread() != null && ac5.previousOrThread() == null) {
                ac5.thread = ac2.previousOrThread();
                final TreeNodePosition treeNodePosition2 = ac5;
                treeNodePosition2.mod += float7 - float10;
            }
            ac = this;
        }
        return ac;
    }
    
    private void moveSubtree(final TreeNodePosition ac, final float float2) {
        final float float3 = (float)(ac.childIndex - this.childIndex);
        if (float3 != 0.0f) {
            ac.change -= float2 / float3;
            this.change += float2 / float3;
        }
        ac.shift += float2;
        ac.y += float2;
        ac.mod += float2;
    }
    
    private TreeNodePosition getAncestor(final TreeNodePosition ac1, final TreeNodePosition ac2) {
        if (this.ancestor != null && ac1.parent.children.contains(this.ancestor)) {
            return this.ancestor;
        }
        return ac2;
    }
    
    private void finalizePosition() {
        if (this.advancement.getDisplay() != null) {
            this.advancement.getDisplay().setLocation((float)this.x, this.y);
        }
        if (!this.children.isEmpty()) {
            for (final TreeNodePosition ac3 : this.children) {
                ac3.finalizePosition();
            }
        }
    }
    
    public static void run(final Advancement q) {
        if (q.getDisplay() == null) {
            throw new IllegalArgumentException("Can't position children of an invisible root!");
        }
        final TreeNodePosition ac2 = new TreeNodePosition(q, null, null, 1, 0);
        ac2.firstWalk();
        final float float3 = ac2.secondWalk(0.0f, 0, ac2.y);
        if (float3 < 0.0f) {
            ac2.thirdWalk(-float3);
        }
        ac2.finalizePosition();
    }
}
