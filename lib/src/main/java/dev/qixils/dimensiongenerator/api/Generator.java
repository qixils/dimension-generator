package dev.qixils.dimensiongenerator.api;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalLong;
import java.util.Random;

/**
 * A generator of random objects.
 *
 * @since 1.0.0
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public interface Generator {

    /**
     * The default random number generator used to create random seeds.
     *
     * @since 1.0.0
     */
    @NotNull Random DEFAULT_RANDOM = new Random();

    /**
     * Creates a new random number generator with the given seed.
     *
     * @param seed the seed to use, or empty to use {@link #DEFAULT_RANDOM}
     * @return a new random number generator
     * @since 1.0.0
     */
    default @NotNull Random createRandom(@NotNull OptionalLong seed) {
        return seed.isPresent() ? new Random(seed.getAsLong()) : DEFAULT_RANDOM;
    }

    /**
     * Generates a random {@link ServerWorld world}.
     *
     * @param seed the seed to use
     * @return a random world
     * @since 1.0.0
     */
    @NotNull ServerWorld generateWorld(long seed);

    /**
     * Generates a random {@link ServerWorld world}.
     *
     * @return a random world
     * @since 1.0.0
     */
    @ApiStatus.NonExtendable
    default @NotNull ServerWorld generateWorld() {
        return generateWorld(DEFAULT_RANDOM.nextLong());
    }

    /**
     * Generates a random {@link DimensionOptions dimension}.
     *
     * @param seed the seed to use, or empty to use {@link #DEFAULT_RANDOM}
     * @return a random dimension
     * @since 1.0.0
     */
    @NotNull DimensionOptions generateDimension(@NotNull OptionalLong seed);

    /**
     * Generates a random {@link DimensionOptions dimension}.
     *
     * @return a random dimension
     * @since 1.0.0
     */
    @ApiStatus.NonExtendable
    default @NotNull DimensionOptions generateDimension() {
        return generateDimension(OptionalLong.empty());
    }

    /**
     * Generates a random {@link DimensionType dimension type}.
     *
     * @param seed the seed to use, or empty to use {@link #DEFAULT_RANDOM}
     * @return a random dimension type
     * @since 1.0.0
     */
    @NotNull DimensionType generateDimensionType(@NotNull OptionalLong seed);

    /**
     * Generates a random {@link DimensionType dimension type}.
     *
     * @return a random dimension type
     * @since 1.0.0
     */
    @ApiStatus.NonExtendable
    default @NotNull DimensionType generateDimensionType() {
        return generateDimensionType(OptionalLong.empty());
    }

    /**
     * Generates random {@link DimensionType.MonsterSettings monster settings}
     * for a {@link DimensionType dimension type}.
     *
     * @param seed the seed to use, or empty to use {@link #DEFAULT_RANDOM}
     * @return random monster settings
     * @since 1.0.0
     */
    DimensionType.@NotNull MonsterSettings generateMonsterSettings(@NotNull OptionalLong seed);

    /**
     * Generates random {@link DimensionType.MonsterSettings monster settings}
     * for a {@link DimensionType dimension type}.
     *
     * @return random monster settings
     * @since 1.0.0
     */
    @ApiStatus.NonExtendable
    default DimensionType.@NotNull MonsterSettings generateMonsterSettings() {
        return generateMonsterSettings(OptionalLong.empty());
    }

    /**
     * Generates a random {@link IntProvider int provider} from the given inclusive range.
     *
     * @param seed the seed to use, or empty to use {@link #DEFAULT_RANDOM}
     * @param min the minimum value, inclusive
     * @param max the maximum value, inclusive
     * @return a random int provider
     * @since 1.0.0
     */
    @NotNull IntProvider generateIntProvider(@NotNull OptionalLong seed, int min, int max);

    /**
     * Generates a random {@link IntProvider int provider} from the given inclusive range.
     *
     * @param min the minimum value, inclusive
     * @param max the maximum value, inclusive
     * @return a random int provider
     * @since 1.0.0
     */
    @ApiStatus.NonExtendable
    default @NotNull IntProvider generateIntProvider(int min, int max) {
        return generateIntProvider(OptionalLong.empty(), min, max);
    }

    /**
     * Generates a random {@link ChunkGenerator chunk generator}.
     *
     * @param seed the seed to use, or empty to use {@link #DEFAULT_RANDOM}
     * @return a random chunk generator
     * @since 1.0.0
     */
    @NotNull ChunkGenerator generateChunkGenerator(@NotNull OptionalLong seed);

    /**
     * Generates a random {@link ChunkGenerator chunk generator}.
     *
     * @return a random chunk generator
     * @since 1.0.0
     */
    @ApiStatus.NonExtendable
    default @NotNull ChunkGenerator generateChunkGenerator() {
        return generateChunkGenerator(OptionalLong.empty());
    }

    /**
     * Generates a random {@link BiomeSource biome source}.
     *
     * @param seed the seed to use, or empty to use {@link #DEFAULT_RANDOM}
     * @return a random biome source
     * @since 1.0.0
     */
    @NotNull BiomeSource generateBiomeSource(@NotNull OptionalLong seed);

    /**
     * Generates a random {@link BiomeSource biome source}.
     *
     * @return a random biome source
     * @since 1.0.0
     */
    @ApiStatus.NonExtendable
    default @NotNull BiomeSource generateBiomeSource() {
        return generateBiomeSource(OptionalLong.empty());
    }

    /**
     * Generates a random {@link Biome biome}.
     *
     * @param seed the seed to use, or empty to use {@link #DEFAULT_RANDOM}
     * @return a random biome
     * @since 1.0.0
     */
    @NotNull Biome generateBiome(@NotNull OptionalLong seed);

    /**
     * Generates a random {@link Biome biome}.
     *
     * @return a random biome
     * @since 1.0.0
     */
    @ApiStatus.NonExtendable
    default @NotNull Biome generateBiome() {
        return generateBiome(OptionalLong.empty());
    }
}
