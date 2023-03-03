package dev.qixils.dimensiongenerator.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
    @Accessor("workerExecutor")
    Executor getWorkerExecutor();

    @Accessor("session")
    LevelStorage.Session getSession();

    @Accessor("saveProperties")
    SaveProperties getSaveProperties();

    @Accessor("worlds")
    Map<RegistryKey<World>, ServerWorld> getWorlds();
}
