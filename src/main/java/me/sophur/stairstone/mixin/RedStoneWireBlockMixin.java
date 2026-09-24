package me.sophur.stairstone.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {
    public boolean getCanConnect(Direction upwardDirection, BlockState separatingBlockState) {
        if (upwardDirection.getAxis() == Direction.Axis.Y) return true;

        Block separatingBlock = separatingBlockState.getBlock();

        if (separatingBlock instanceof StairBlock) {

        } else if (separatingBlock instanceof SlabBlock) {
            // block connection if bottom slab or double slab
            return separatingBlockState.getValue(SlabBlock.TYPE) == SlabType.TOP;
        }
        return true;
    }

    @WrapMethod(method = "getConnectingSide(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/state/properties/RedstoneSide;")
    public RedstoneSide wrapGetConnectingSide(BlockGetter level, BlockPos pos, Direction direction, boolean canConnectUp, Operation<RedstoneSide> original) {
        if (canConnectUp) {
            BlockPos posAbove = pos.above();
            BlockState stateAbove = level.getBlockState(posAbove);
            canConnectUp = getCanConnect(direction, stateAbove);
        }

        return original.call(level, pos, direction, canConnectUp);
    }

    @ModifyExpressionValue(method = "getConnectingSide(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/state/properties/RedstoneSide;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
    public boolean injectGetConnectingSide(boolean original, @Local(name = "relativeState") BlockState relativeState, @Local(argsOnly = true, name = "direction") Direction direction) {
        if (!getCanConnect(direction.getOpposite(), relativeState)) return true;
        return original;
    }
}
