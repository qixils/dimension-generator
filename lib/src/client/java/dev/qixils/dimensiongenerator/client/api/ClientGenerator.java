package dev.qixils.dimensiongenerator.client.api;

import dev.qixils.dimensiongenerator.api.Generator;
import net.minecraft.client.render.DimensionEffects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;

/**
 * A generator of random objects available to the client.
 *
 * @since 1.0.0
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface ClientGenerator extends Generator {

    /**
     * Generates random {@link DimensionEffects dimension effects}.
     *
     * @param seed the seed to use, or empty to use {@link #DEFAULT_RANDOM}
     * @return a random dimension effects
     * @since 1.0.0
     */
    @NotNull DimensionEffects generateDimensionEffects(@NotNull OptionalLong seed);

    /**
     * Generates random {@link DimensionEffects dimension effects}.
     *
     * @return a random dimension effects
     * @since 1.0.0
     */
    @ApiStatus.NonExtendable
    default @NotNull DimensionEffects generateDimensionEffects() {
        return generateDimensionEffects(OptionalLong.empty());
    }
}
