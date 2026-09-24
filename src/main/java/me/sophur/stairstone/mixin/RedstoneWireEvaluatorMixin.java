package me.sophur.stairstone.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.sophur.stairstone.StairstoneMain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.RedstoneWireEvaluator;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RedstoneWireEvaluator.class)
public class RedstoneWireEvaluatorMixin {
    @Definition(id = "isRedstoneConductor", method = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z")
    @Definition(id = "level", local = @Local(type = Level.class, name = "level", argsOnly = true))
    @Definition(id = "abovePos", local = @Local(type = BlockPos.class, name = "abovePos"))
    @Expression("?.isRedstoneConductor(level, abovePos)")
    @WrapOperation(method = "getIncomingWireSignal", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean wrapGetIncomingWireSignalUpwardBlocking(BlockState instance, BlockGetter blockGetter, BlockPos blockPos, Operation<Boolean> original, @Local(name = "direction") Direction direction) {
        // redstone signal going DOWN
        // this wraps the check for a conductive block preventing the signal from connecting
        if (StairstoneMain.shouldBlockConnection(direction, blockGetter, blockPos, instance))
            return true;
        return original.call(instance, blockGetter, blockPos);
    }

    @Definition(id = "isRedstoneConductor", method = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z")
    @Definition(id = "neighborState", local = @Local(type = BlockState.class, name = "neighborState"))
    @Definition(id = "level", local = @Local(type = Level.class, name = "level", argsOnly = true))
    @Definition(id = "neighborPos", local = @Local(type = BlockPos.class, name = "neighborPos"))
    @Expression("neighborState.isRedstoneConductor(level, neighborPos)")
    @WrapOperation(method = "getIncomingWireSignal", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 1))
    private boolean wrapGetIncomingWireSignalDownwardBlocking(BlockState instance, BlockGetter blockGetter, BlockPos blockPos, Operation<Boolean> original, @Local(name = "direction") @NonNull Direction direction) {
        // redstone signal going UP
        // this wraps the check for a conductive block preventing the signal from connecting
        if (StairstoneMain.shouldBlockConnection(direction.getOpposite(), blockGetter, blockPos, instance))
            return true;
        return original.call(instance, blockGetter, blockPos);
    }

    @Definition(id = "isRedstoneConductor", method = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z")
    @Definition(id = "neighborState", local = @Local(type = BlockState.class, name = "neighborState"))
    @Definition(id = "level", local = @Local(type = Level.class, name = "level", argsOnly = true))
    @Definition(id = "neighborPos", local = @Local(type = BlockPos.class, name = "neighborPos"))
    @Expression("neighborState.isRedstoneConductor(level, neighborPos)")
    @WrapOperation(method = "getIncomingWireSignal", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private boolean wrapGetIncomingWireSignalDownwardConductiveSupportingBlock(BlockState instance, BlockGetter blockGetter, BlockPos blockPos, Operation<Boolean> original, @Local(name = "direction") @NonNull Direction direction) {
        // redstone signal going DOWN
        // this wraps the check for a conductive block underneath the
        if (StairstoneMain.shouldAllowConnectDown(direction, blockGetter, blockPos, instance))
            return true;
        return original.call(instance, blockGetter, blockPos);
    }
}
