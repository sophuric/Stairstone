package me.sophur.stairstone;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class StairstoneMain implements ModInitializer {
    public static final String MOD_ID = "Stairstone";
    public static final String MOD_ID_LOWER = "stairstone";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final TagKey<Block> REDSTONE_BLOCKING_DIRECTIONAL = TagKey.create(Registries.BLOCK, identifier("redstone_blocking_directional"));

    public static boolean isDirectional(BlockState blockState) {
        return blockState.is(REDSTONE_BLOCKING_DIRECTIONAL);
    }

    public static boolean getCanConnect(Direction upwardDirection, BlockGetter level, BlockPos blockingBlockPos, BlockState blockingBlockState) {
        if (upwardDirection.getAxis() == Direction.Axis.Y) return true;
        if (!isDirectional(blockingBlockState)) return true;

        // block connection if bottom face OR side face is solid
        boolean blockedBottom = getIfSolidFace(Direction.DOWN, level, blockingBlockPos, blockingBlockState),
                blockedSide = getIfSolidFace(upwardDirection, level, blockingBlockPos, blockingBlockState);
        return !blockedBottom && !blockedSide;
    }

    public static boolean shouldForceAllowConnectDown(Direction upwardDirection, BlockGetter level, BlockPos supportingBlockPos, BlockState supportingBlockState) {
        if (upwardDirection.getAxis() == Direction.Axis.Y) return false;
        if (!isDirectional(supportingBlockState)) return false;

        return getIfSolidFace(upwardDirection.getOpposite(), level, supportingBlockPos, supportingBlockState);
    }

    public static boolean getIfSolidFace(Direction direction, BlockGetter level, BlockPos blockPos, BlockState blockState) {
        // wrapper method if I ever want to change how this is implemented
        return blockState.isFaceSturdy(level, blockPos, direction, SupportType.FULL);
    }

    @Override
    public void onInitialize() {
    }

    public static Identifier identifier(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID_LOWER, name);
    }

    public static Path getModDirectory() throws RuntimeException {
        var directory = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
        //noinspection ResultOfMethodCallIgnored
        directory.toFile().mkdirs();
        return directory;
    }
}