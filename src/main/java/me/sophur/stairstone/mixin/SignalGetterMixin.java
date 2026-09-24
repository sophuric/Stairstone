package me.sophur.stairstone.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.sophur.stairstone.StairstoneMain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static net.minecraft.world.level.SignalGetter.DIRECTIONS;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin extends BlockGetter {
    @Shadow
    int getBestNeighborSignal(final BlockPos pos);

    @Shadow
    int getSignal(final BlockPos pos, final Direction direction);

    @Shadow
    int getDirectSignal(final BlockPos pos, final Direction direction);

    @WrapMethod(method = "hasNeighborSignal")
    private boolean wrapHasNeighborSignal(BlockPos blockPos, Operation<Boolean> original) {
        var state = this.getBlockState(blockPos);
        if (!StairstoneMain.isDirectional(state)) return original.call(blockPos);

        for (Direction direction : DIRECTIONS) {
            if (!StairstoneMain.getIfSolidFace(direction, this, blockPos, state)) continue;
            if (getSignal(blockPos.relative(direction), direction) > 0) return true;
        }

        return false;
    }

    @WrapMethod(method = "getBestNeighborSignal")
    private int modifyGetBestNeighborSignal(BlockPos pos, Operation<Integer> original) {
        var state = this.getBlockState(pos);
        if (!StairstoneMain.isDirectional(state)) return original.call(pos);

        int best = 0;

        for (Direction direction : DIRECTIONS) {
            if (!StairstoneMain.getIfSolidFace(direction, this, pos, state)) continue;

            int signal = getSignal(pos.relative(direction), direction);
            if (signal >= 15) return 15;
            if (signal > best) best = signal;
        }

        return best;
    }

    @WrapMethod(method = "getDirectSignalTo")
    private int wrapGetDirectSignalTo(BlockPos pos, Operation<Integer> original) {
        var state = this.getBlockState(pos);
        if (!StairstoneMain.isDirectional(state)) return original.call(pos);

        int best = 0;

        for (Direction direction : DIRECTIONS) {
            if (!StairstoneMain.getIfSolidFace(direction, this, pos, state)) continue;

            int signal = getDirectSignal(pos.relative(direction), direction);
            if (signal >= 15) return 15;
            if (signal > best) best = signal;
        }

        return best;
    }

    @Definition(id = "state", local = @Local(type = BlockState.class, name = "state"))
    @Definition(id = "isRedstoneConductor", method = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z")
    @Definition(id = "pos", local = @Local(type = BlockPos.class, name = "pos", argsOnly = true))
    @Expression("state.isRedstoneConductor(this, pos)")
    @WrapOperation(method = "getSignal", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean wrapGetSignal(BlockState instance, BlockGetter blockGetter, BlockPos blockPos, Operation<Boolean> original, @Local(name = "direction", argsOnly = true) Direction direction) {
        if (StairstoneMain.isDirectional(instance)) {
            return StairstoneMain.getIfSolidFace(direction.getOpposite(),
                    this, blockPos, instance);
        }
        return original.call(instance, blockGetter, blockPos);
    }
}
