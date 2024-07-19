package dev.qixils.dimensiongenerator.client;

import dev.qixils.dimensiongenerator.DimensionGenerator;
import dev.qixils.dimensiongenerator.client.api.ClientGenerator;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.OptionalLong;
import java.util.Random;
import java.util.function.BiFunction;

import static dev.qixils.dimensiongenerator.util.RandomUtils.randomFromArray;
import static dev.qixils.dimensiongenerator.util.RandomUtils.randomFromEnum;
import static dev.qixils.dimensiongenerator.util.RegistryUtils.getRegisteredValue;

public class DimensionGeneratorClient implements ClientModInitializer, ClientGenerator {
    public static final Logger LOGGER = DimensionGenerator.LOGGER;
    public static ClientGenerator INSTANCE = null;
    private DimensionEffects[] vanillaEffects = null;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing client");
        INSTANCE = this;
    }

    @Override
    public @NotNull DimensionEffects generateDimensionEffects(@NotNull OptionalLong seed) {
        // TODO: S2C packet to define the current dimension effects

        Random rng = createRandom(seed);
        if (vanillaEffects == null) {
            vanillaEffects = new DimensionEffects[]{
                    DimensionEffects.byDimensionType(getRegisteredValue(DimensionTypes.OVERWORLD)),
                    DimensionEffects.byDimensionType(getRegisteredValue(DimensionTypes.THE_NETHER)),
                    DimensionEffects.byDimensionType(getRegisteredValue(DimensionTypes.THE_END))
            };
        }

        // fields
        float cloudsHeight = rng.nextBoolean()
                ? Float.NaN
                : rng.nextFloat(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT + 1);
        boolean alternateSkyColor = rng.nextBoolean();
        DimensionEffects.SkyType skyType = randomFromEnum(rng, DimensionEffects.SkyType.class);
        int lightType = rng.nextInt(3);

        // abstract methods
        // TODO: create custom implementations of these
        BiFunction<Vec3d, Float, Vec3d> adjustFogColor = randomFromArray(rng, vanillaEffects)::adjustFogColor;
        BiFunction<Integer, Integer, Boolean> useThickFog = randomFromArray(rng, vanillaEffects)::useThickFog;
        BiFunction<Float, Float, float[]> getFogColorOverride = randomFromArray(rng, vanillaEffects)::getFogColorOverride;

        // create
        return new DimensionEffects(cloudsHeight, alternateSkyColor, skyType, lightType == 1, lightType == 2) {
            @Override
            public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
                return adjustFogColor.apply(color, sunHeight);
            }

            @Override
            public boolean useThickFog(int camX, int camY) {
                return useThickFog.apply(camX, camY);
            }

            @Override
            public float @Nullable [] getFogColorOverride(float skyAngle, float tickDelta) {
                return getFogColorOverride.apply(skyAngle, tickDelta);
            }
        };
    }

    // server-side methods

    @Override
    public @NotNull ServerWorld generateWorld(long seed) {
        return DimensionGenerator.INSTANCE.generateWorld(seed);
    }

    @Override
    public @NotNull DimensionOptions generateDimension(@NotNull OptionalLong seed) {
        return DimensionGenerator.INSTANCE.generateDimension(seed);
    }

    @Override
    public @NotNull DimensionType generateDimensionType(@NotNull OptionalLong seed) {
        return DimensionGenerator.INSTANCE.generateDimensionType(seed);
    }

    @Override
    public @NotNull IntProvider generateIntProvider(@NotNull OptionalLong seed, int min, int max) {
        return DimensionGenerator.INSTANCE.generateIntProvider(seed, min, max);
    }

    @Override
    public @NotNull ChunkGenerator generateChunkGenerator(@NotNull OptionalLong seed) {
        return DimensionGenerator.INSTANCE.generateChunkGenerator(seed);
    }

    @Override
    public @NotNull BiomeSource generateBiomeSource(@NotNull OptionalLong seed) {
        return DimensionGenerator.INSTANCE.generateBiomeSource(seed);
    }

    @Override
    public @NotNull Biome generateBiome(@NotNull OptionalLong seed) {
        return DimensionGenerator.INSTANCE.generateBiome(seed);
    }
}
