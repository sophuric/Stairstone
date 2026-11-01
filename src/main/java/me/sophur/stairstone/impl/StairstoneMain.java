package me.sophur.stairstone.impl;

import me.sophur.stairstone.api.v2.StairstoneBehaviour;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class StairstoneMain implements ModInitializer {
    public static final String MOD_ID = "Stairstone";
    public static final String MOD_ID_LOWER = "stairstone";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean shouldBlockConnection(Direction upwardDirection, BlockGetter level, BlockPos blockingBlockPos, BlockState blockingBlockState) {
        if (upwardDirection.getAxis() == Direction.Axis.Y) return false;

        var behaviour = StairstoneBehaviour.getConnectivityBehaviour(blockingBlockState.getBlock());

        // block connection if bottom face OR side face is solid
        boolean blockedBottom = behaviour.isSolidFaceForRedstoneWireConnectivity(Direction.DOWN, level, blockingBlockPos, blockingBlockState),
                blockedSide = behaviour.isSolidFaceForRedstoneWireConnectivity(upwardDirection, level, blockingBlockPos, blockingBlockState);
        return blockedBottom || blockedSide;
    }

    public static boolean shouldAllowConnectDown(Direction upwardDirection, BlockGetter level, BlockPos supportingBlockPos, BlockState supportingBlockState) {
        if (upwardDirection.getAxis() == Direction.Axis.Y) return false;

        var behaviour = StairstoneBehaviour.getConnectivityBehaviour(supportingBlockState.getBlock());

        return behaviour.isSolidFaceForRedstoneWireConnectivity(upwardDirection.getOpposite(), level, supportingBlockPos, supportingBlockState);
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
