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
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {
    @WrapMethod(method = "getConnectingSide(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/state/properties/RedstoneSide;")
    private RedstoneSide wrapGetConnectingSide(BlockGetter level, BlockPos pos, Direction direction, boolean canConnectUp, Operation<RedstoneSide> original) {
        // redstone signal going UP

        if (canConnectUp) {
            BlockPos posAbove = pos.above();
            BlockState stateAbove = level.getBlockState(posAbove);
            canConnectUp = StairstoneMain.getCanConnect(direction, level, posAbove, stateAbove);
        }

        return original.call(level, pos, direction, canConnectUp);
    }

    @Definition(id = "relativeState", local = @Local(type = BlockState.class, name = "relativeState"))
    @Definition(id = "isRedstoneConductor", method = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z")
    @Definition(id = "level", local = @Local(type = BlockGetter.class, name = "level", argsOnly = true))
    @Definition(id = "relativePos", local = @Local(type = BlockPos.class, name = "relativePos"))
    @Expression("relativeState.isRedstoneConductor(level,relativePos)")
    @WrapOperation(method = "getConnectingSide(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/state/properties/RedstoneSide;", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean wrapGetConnectingSide(BlockState instance, BlockGetter blockGetter, BlockPos blockPos, Operation<Boolean> original, @Local(argsOnly = true, name = "direction") Direction direction) {
        // redstone signal going DOWN

        if (!StairstoneMain.getCanConnect(direction.getOpposite(), blockGetter, blockPos, instance)) return true;
        return original.call(instance, blockGetter, blockPos);
    }
}
