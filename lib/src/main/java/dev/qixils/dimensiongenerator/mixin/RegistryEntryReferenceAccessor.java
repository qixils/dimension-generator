package dev.qixils.dimensiongenerator.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.registry.entry.RegistryEntry$Reference")
public interface RegistryEntryReferenceAccessor {
    @Invoker("<init>")
    static <T> RegistryEntry.Reference<T> invokeNew(RegistryEntry.Reference.Type referenceType, RegistryEntryOwner<T> owner, @Nullable RegistryKey<T> registryKey, @Nullable T value) {
        throw new AssertionError();
    }
}
