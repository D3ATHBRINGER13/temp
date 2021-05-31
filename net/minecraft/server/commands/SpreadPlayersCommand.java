package net.minecraft.server.commands;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import net.minecraft.util.Mth;
import com.google.common.collect.Maps;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.server.level.ServerLevel;
import java.util.Iterator;
import net.minecraft.world.scores.Team;
import java.util.Set;
import net.minecraft.world.entity.player.Player;
import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import java.util.Locale;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import net.minecraft.world.phys.Vec2;
import net.minecraft.commands.arguments.EntityArgument;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;

public class SpreadPlayersCommand {
    private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_TEAMS;
    private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_ENTITIES;
    
    public static void register(final CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spreadplayers").requires(cd -> cd.hasPermission(2))).then(Commands.argument("center", (com.mojang.brigadier.arguments.ArgumentType<Object>)Vec2Argument.vec2()).then(Commands.argument("spreadDistance", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(0.0f)).then(Commands.argument("maxRange", (com.mojang.brigadier.arguments.ArgumentType<Object>)FloatArgumentType.floatArg(1.0f)).then(Commands.argument("respectTeams", (com.mojang.brigadier.arguments.ArgumentType<Object>)BoolArgumentType.bool()).then(Commands.argument("targets", (com.mojang.brigadier.arguments.ArgumentType<Object>)EntityArgument.entities()).executes(commandContext -> spreadPlayers((CommandSourceStack)commandContext.getSource(), Vec2Argument.getVec2((CommandContext<CommandSourceStack>)commandContext, "center"), FloatArgumentType.getFloat(commandContext, "spreadDistance"), FloatArgumentType.getFloat(commandContext, "maxRange"), BoolArgumentType.getBool(commandContext, "respectTeams"), EntityArgument.getEntities((CommandContext<CommandSourceStack>)commandContext, "targets")))))))));
    }
    
    private static int spreadPlayers(final CommandSourceStack cd, final Vec2 csh, final float float3, final float float4, final boolean boolean5, final Collection<? extends Entity> collection) throws CommandSyntaxException {
        final Random random7 = new Random();
        final double double8 = csh.x - float4;
        final double double9 = csh.y - float4;
        final double double10 = csh.x + float4;
        final double double11 = csh.y + float4;
        final Position[] arr16 = createInitialPositions(random7, boolean5 ? getNumberOfTeams(collection) : collection.size(), double8, double9, double10, double11);
        spreadPositions(csh, float3, cd.getLevel(), random7, double8, double9, double10, double11, arr16, boolean5);
        final double double12 = setPlayerPositions(collection, cd.getLevel(), arr16, boolean5);
        cd.sendSuccess(new TranslatableComponent(new StringBuilder().append("commands.spreadplayers.success.").append(boolean5 ? "teams" : "entities").toString(), new Object[] { arr16.length, csh.x, csh.y, String.format(Locale.ROOT, "%.2f", new Object[] { double12 }) }), true);
        return arr16.length;
    }
    
    private static int getNumberOfTeams(final Collection<? extends Entity> collection) {
        final Set<Team> set2 = (Set<Team>)Sets.newHashSet();
        for (final Entity aio4 : collection) {
            if (aio4 instanceof Player) {
                set2.add(aio4.getTeam());
            }
            else {
                set2.add(null);
            }
        }
        return set2.size();
    }
    
    private static void spreadPositions(final Vec2 csh, final double double2, final ServerLevel vk, final Random random, final double double5, final double double6, final double double7, final double double8, final Position[] arr, final boolean boolean10) throws CommandSyntaxException {
        boolean boolean11 = true;
        double double9 = 3.4028234663852886E38;
        int integer17;
        for (integer17 = 0; integer17 < 10000 && boolean11; ++integer17) {
            boolean11 = false;
            double9 = 3.4028234663852886E38;
            for (int integer18 = 0; integer18 < arr.length; ++integer18) {
                final Position a21 = arr[integer18];
                int integer19 = 0;
                final Position a22 = new Position();
                for (int integer20 = 0; integer20 < arr.length; ++integer20) {
                    if (integer18 != integer20) {
                        final Position a23 = arr[integer20];
                        final double double10 = a21.dist(a23);
                        double9 = Math.min(double10, double9);
                        if (double10 < double2) {
                            ++integer19;
                            a22.x += a23.x - a21.x;
                            a22.z += a23.z - a21.z;
                        }
                    }
                }
                if (integer19 > 0) {
                    a22.x /= integer19;
                    a22.z /= integer19;
                    final double double11 = a22.getLength();
                    if (double11 > 0.0) {
                        a22.normalize();
                        a21.moveAway(a22);
                    }
                    else {
                        a21.randomize(random, double5, double6, double7, double8);
                    }
                    boolean11 = true;
                }
                if (a21.clamp(double5, double6, double7, double8)) {
                    boolean11 = true;
                }
            }
            if (!boolean11) {
                for (final Position a22 : arr) {
                    if (!a22.isSafe(vk)) {
                        a22.randomize(random, double5, double6, double7, double8);
                        boolean11 = true;
                    }
                }
            }
        }
        if (double9 == 3.4028234663852886E38) {
            double9 = 0.0;
        }
        if (integer17 < 10000) {
            return;
        }
        if (boolean10) {
            throw SpreadPlayersCommand.ERROR_FAILED_TO_SPREAD_TEAMS.create(arr.length, csh.x, csh.y, String.format(Locale.ROOT, "%.2f", new Object[] { double9 }));
        }
        throw SpreadPlayersCommand.ERROR_FAILED_TO_SPREAD_ENTITIES.create(arr.length, csh.x, csh.y, String.format(Locale.ROOT, "%.2f", new Object[] { double9 }));
    }
    
    private static double setPlayerPositions(final Collection<? extends Entity> collection, final ServerLevel vk, final Position[] arr, final boolean boolean4) {
        double double5 = 0.0;
        int integer7 = 0;
        final Map<Team, Position> map8 = (Map<Team, Position>)Maps.newHashMap();
        for (final Entity aio10 : collection) {
            Position a11;
            if (boolean4) {
                final Team ctk12 = (aio10 instanceof Player) ? aio10.getTeam() : null;
                if (!map8.containsKey(ctk12)) {
                    map8.put(ctk12, arr[integer7++]);
                }
                a11 = (Position)map8.get(ctk12);
            }
            else {
                a11 = arr[integer7++];
            }
            aio10.teleportToWithTicket(Mth.floor(a11.x) + 0.5f, a11.getSpawnY(vk), Mth.floor(a11.z) + 0.5);
            double double6 = Double.MAX_VALUE;
            for (final Position a12 : arr) {
                if (a11 != a12) {
                    final double double7 = a11.dist(a12);
                    double6 = Math.min(double7, double6);
                }
            }
            double5 += double6;
        }
        if (collection.size() < 2) {
            return 0.0;
        }
        double5 /= collection.size();
        return double5;
    }
    
    private static Position[] createInitialPositions(final Random random, final int integer, final double double3, final double double4, final double double5, final double double6) {
        final Position[] arr11 = new Position[integer];
        for (int integer2 = 0; integer2 < arr11.length; ++integer2) {
            final Position a13 = new Position();
            a13.randomize(random, double3, double4, double5, double6);
            arr11[integer2] = a13;
        }
        return arr11;
    }
    
    static {
        ERROR_FAILED_TO_SPREAD_TEAMS = new Dynamic4CommandExceptionType((object1, object2, object3, object4) -> new TranslatableComponent("commands.spreadplayers.failed.teams", new Object[] { object1, object2, object3, object4 }));
        ERROR_FAILED_TO_SPREAD_ENTITIES = new Dynamic4CommandExceptionType((object1, object2, object3, object4) -> new TranslatableComponent("commands.spreadplayers.failed.entities", new Object[] { object1, object2, object3, object4 }));
    }
    
    static class Position {
        private double x;
        private double z;
        
        double dist(final Position a) {
            final double double3 = this.x - a.x;
            final double double4 = this.z - a.z;
            return Math.sqrt(double3 * double3 + double4 * double4);
        }
        
        void normalize() {
            final double double2 = this.getLength();
            this.x /= double2;
            this.z /= double2;
        }
        
        float getLength() {
            return Mth.sqrt(this.x * this.x + this.z * this.z);
        }
        
        public void moveAway(final Position a) {
            this.x -= a.x;
            this.z -= a.z;
        }
        
        public boolean clamp(final double double1, final double double2, final double double3, final double double4) {
            boolean boolean10 = false;
            if (this.x < double1) {
                this.x = double1;
                boolean10 = true;
            }
            else if (this.x > double3) {
                this.x = double3;
                boolean10 = true;
            }
            if (this.z < double2) {
                this.z = double2;
                boolean10 = true;
            }
            else if (this.z > double4) {
                this.z = double4;
                boolean10 = true;
            }
            return boolean10;
        }
        
        public int getSpawnY(final BlockGetter bhb) {
            BlockPos ew3 = new BlockPos(this.x, 256.0, this.z);
            while (ew3.getY() > 0) {
                ew3 = ew3.below();
                if (!bhb.getBlockState(ew3).isAir()) {
                    return ew3.getY() + 1;
                }
            }
            return 257;
        }
        
        public boolean isSafe(final BlockGetter bhb) {
            BlockPos ew3 = new BlockPos(this.x, 256.0, this.z);
            while (ew3.getY() > 0) {
                ew3 = ew3.below();
                final BlockState bvt4 = bhb.getBlockState(ew3);
                if (!bvt4.isAir()) {
                    final Material clo5 = bvt4.getMaterial();
                    return !clo5.isLiquid() && clo5 != Material.FIRE;
                }
            }
            return false;
        }
        
        public void randomize(final Random random, final double double2, final double double3, final double double4, final double double5) {
            this.x = Mth.nextDouble(random, double2, double4);
            this.z = Mth.nextDouble(random, double3, double5);
        }
    }
}
