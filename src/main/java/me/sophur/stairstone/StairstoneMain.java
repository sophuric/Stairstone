package me.sophur.stairstone;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
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

    public static boolean getCanConnect(Direction upwardDirection, BlockGetter level, BlockPos separatingBlockPos, BlockState separatingBlockState) {
        if (upwardDirection.getAxis() == Direction.Axis.Y) return true;

        Block separatingBlock = separatingBlockState.getBlock();

        if (separatingBlock instanceof StairBlock || separatingBlock instanceof SlabBlock) {
            // block connection if bottom face OR side face is solid
            boolean blockedBottom = separatingBlockState.isFaceSturdy(level, separatingBlockPos, Direction.DOWN, SupportType.FULL),
                    blockedSide = separatingBlockState.isFaceSturdy(level, separatingBlockPos, upwardDirection, SupportType.FULL);
            if (blockedBottom || blockedSide) return false;
        }
        return true;
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