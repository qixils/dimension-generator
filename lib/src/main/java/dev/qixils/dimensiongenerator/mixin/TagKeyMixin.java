package dev.qixils.dimensiongenerator.mixin;

import dev.qixils.dimensiongenerator.util.RegistryUtils;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TagKey.class)
public class TagKeyMixin {
    @Inject(method = "of", at = @At("RETURN"))
    private static void of(RegistryKey registry, Identifier id, CallbackInfoReturnable<TagKey> cir) {
        RegistryUtils.TAG_KEYS.add(cir.getReturnValue());
    }
}
