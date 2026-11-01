package me.sophur.stairstone.api.v2;

import me.sophur.stairstone.impl.StairstoneMain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class StairstoneBehaviour {
    private StairstoneBehaviour() {
    }

    @FunctionalInterface
    public interface RedstoneWireConnectivity {
        boolean isSolidFaceForRedstoneWireConnectivity(Direction direction, BlockGetter level, BlockPos blockPos, BlockState blockState);
    }

    private static final TagKey<Block> CONNECTIVITY_TAG =
            TagKey.create(Registries.BLOCK, StairstoneMain.identifier("directional_redstone_connects"));
    public static final RedstoneWireConnectivity CONNECTIVITY_DEFAULT_VANILLA =
            ((_, level, blockPos, blockState) ->
                    blockState.isRedstoneConductor(level, blockPos));
    public static final RedstoneWireConnectivity CONNECTIVITY_DEFAULT_STAIRSTONE =
            ((direction, level, blockPos, blockState) ->
                    blockState.isFaceSturdy(level, blockPos, direction, SupportType.FULL));

    @SuppressWarnings("deprecation")
    public static @NotNull RedstoneWireConnectivity getConnectivityBehaviour(Block block) {
        if (block instanceof RedstoneWireConnectivity c) return c;
        if (block.builtInRegistryHolder().is(CONNECTIVITY_TAG)) return CONNECTIVITY_DEFAULT_STAIRSTONE;
        return CONNECTIVITY_DEFAULT_VANILLA;
    }

    @FunctionalInterface
    public interface RedstonePower {
        boolean isSolidFaceForRedstonePower(Direction direction, BlockGetter level, BlockPos blockPos, BlockState blockState);
    }

    private static final TagKey<Block> POWER_TAG =
            TagKey.create(Registries.BLOCK, StairstoneMain.identifier("directional_redstone_power"));
    public static final RedstonePower POWER_DEFAULT_VANILLA =
            ((_, level, blockPos, blockState) -> blockState.isRedstoneConductor(level, blockPos));
    public static final RedstonePower POWER_DEFAULT_STAIRSTONE =
            ((direction, level, blockPos, blockState) ->
                    blockState.isFaceSturdy(level, blockPos, direction, SupportType.FULL));

    @SuppressWarnings("deprecation")
    public static @NotNull RedstonePower getPowerBehaviour(Block block) {
        if (block instanceof RedstonePower c) return c;
        if (block.builtInRegistryHolder().is(POWER_TAG)) return POWER_DEFAULT_STAIRSTONE;
        return POWER_DEFAULT_VANILLA;
    }
}
