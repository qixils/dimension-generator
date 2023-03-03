package dev.qixils.dimensiongenerator.mixin;

import dev.qixils.dimensiongenerator.DimensionGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.OptionalLong;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyArgs(method = "createWorlds", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/server/world/ServerWorld;<init>(Lnet/minecraft/server/MinecraftServer;Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/storage/LevelStorage$Session;Lnet/minecraft/world/level/ServerWorldProperties;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/world/dimension/DimensionOptions;Lnet/minecraft/server/WorldGenerationProgressListener;ZJLjava/util/List;Z)V"))
    protected void onCreateCustomWorld(Args args) {
        if (DimensionGenerator.INSTANCE == null)
            return;
        RegistryKey<World> worldKey = args.get(4);
        Identifier worldId = worldKey.getValue();
        if (!worldId.getNamespace().equals(DimensionGenerator.NAMESPACE) || worldId.equals(DimensionGenerator.PLACEHOLDER_ID))
            return;
        long seed;
        try {
            seed = Long.parseLong(worldId.getPath());
        } catch (NumberFormatException e) {
            return;
        }
        args.set(5, DimensionGenerator.INSTANCE.generateDimension(OptionalLong.of(seed)));
    }
}
