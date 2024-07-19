package dev.qixils.dimensiongenerator;

import com.google.common.collect.ImmutableList;
import dev.qixils.dimensiongenerator.api.Generator;
import dev.qixils.dimensiongenerator.mixin.MinecraftServerAccessor;
import dev.qixils.dimensiongenerator.mixin.RegistryEntryReferenceAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.biome.*;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static dev.qixils.dimensiongenerator.util.RandomUtils.*;
import static dev.qixils.dimensiongenerator.util.RegistryUtils.getRegisteredValue;

public class DimensionGenerator implements ModInitializer, Generator {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String NAMESPACE = "dimension-generator"; // aka mod ID
	public static final Identifier PLACEHOLDER_ID = Identifier.of(NAMESPACE, "custom");
	public static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
	public static Generator INSTANCE = null;
	private MinecraftServer server;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Initializing common");
		INSTANCE = this;

		// register dummy dimension
		Registry<DimensionType> dimensionRegistry = getRegisteredValue(RegistryKeys.DIMENSION_TYPE);
		DimensionType placeholder = dimensionRegistry.get(DimensionTypes.OVERWORLD);
		Registry.register(dimensionRegistry, PLACEHOLDER_ID, placeholder);

		// register start listener
		ServerLifecycleEvents.SERVER_STARTING.register(server -> this.server = server);
	}

	@Override
	public @NotNull ServerWorld generateWorld(long seed) {
		if (server == null)
			throw new IllegalStateException("Server has not yet initialized");
		RegistryKey<World> key = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(NAMESPACE, String.valueOf(seed)));
		DimensionOptions dimension = generateDimension(OptionalLong.of(seed));
		WorldGenerationProgressListener logger = new WorldGenerationProgressLogger(11);
		MinecraftServerAccessor accessor = (MinecraftServerAccessor) server;
		ServerWorldProperties properties = new UnmodifiableLevelProperties(accessor.getSaveProperties(), accessor.getSaveProperties().getMainWorldProperties());
		long globalSeed = BiomeAccess.hashSeed(accessor.getSaveProperties().getGeneratorOptions().getSeed());
		ServerWorld world = new ServerWorld(server, accessor.getWorkerExecutor(), accessor.getSession(), properties, key, dimension, logger, false, globalSeed, ImmutableList.of(), false);
		world.getWorldBorder().load(properties.getWorldBorder());
		server.getOverworld().getWorldBorder().addListener(new WorldBorderListener.WorldBorderSyncer(world.getWorldBorder()));
		accessor.getWorlds().put(key, world);
		return world;
	}

	@Override
	public @NotNull DimensionOptions generateDimension(@NotNull OptionalLong seed) {
		Random rng = createRandom(seed);
		Registry<DimensionType> registry = getRegisteredValue(RegistryKeys.DIMENSION_TYPE);
		Identifier id = Identifier.of(NAMESPACE, String.valueOf(rng.nextLong()));
		RegistryKey<DimensionType> key = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, id);
		DimensionType type = generateDimensionType(seed);
		RegistryEntry<DimensionType> entry = RegistryEntryReferenceAccessor.invokeNew(RegistryEntry.Reference.Type.INTRUSIVE, registry.getEntryOwner(), key, type);
		ChunkGenerator generator = generateChunkGenerator(seed);
		return new DimensionOptions(entry, generator);
	}

	@Override
	public @NotNull DimensionType generateDimensionType(@NotNull OptionalLong seed) {
		Random rng = createRandom(seed);
		OptionalLong fixedTime = rng.nextBoolean() ? OptionalLong.of(rng.nextLong(24000)) : OptionalLong.empty();
		boolean hasSkyLight = rng.nextBoolean();
		boolean hasCeiling = rng.nextBoolean(); // TODO: determine how this should work
		boolean ultrawarm = rng.nextBoolean();
		boolean natural = rng.nextBoolean();
		// technically the coord scale bounds are [1.0E-5f, 3.0e7f] but I want to keep this somewhat sane
		double coordinateScale = rng.nextDouble(0.01, 100);
		boolean bedWorks = rng.nextBoolean();
		boolean respawnAnchorWorks = rng.nextBoolean();
		TagKey<Block> infiniburn = rng.nextBoolean() ? null : randomTagFor(rng, RegistryKeys.BLOCK);
		Identifier effects = rng.nextBoolean()
				? randomFromArray(rng, DimensionTypes.OVERWORLD_ID, DimensionTypes.THE_NETHER_ID, DimensionTypes.THE_END_ID)
				: Identifier.of(NAMESPACE, String.valueOf(rng.nextLong())); // will fall back to overworld if client doesn't have the mod
		float ambientLight = rng.nextFloat();

		// monster settings
		boolean piglinSafe = rng.nextBoolean();
		boolean hasRaids = rng.nextBoolean();
		IntProvider monsterSpawnLightTest = generateIntProvider(seed, 0, 16);
		int monsterSpawnBlockLightLimit = rng.nextInt(16); // defaults to min of 0
		DimensionType.MonsterSettings monsterSettings = new DimensionType.MonsterSettings(piglinSafe, hasRaids, monsterSpawnLightTest, monsterSpawnBlockLightLimit);

		// technically the code does not validate that the min Y must be >=MIN_HEIGHT, just the JSON deserializer does
		// however this is probably a bug so I'm going to pretend it does
		int height = rng.nextInt(DimensionType.field_33411 / 16, (DimensionType.MAX_HEIGHT / 16) + 1) * 16;
		int logicalHeight = hasCeiling ? rng.nextInt(height) : height;
		int minY = height == DimensionType.MAX_HEIGHT
				? DimensionType.MIN_HEIGHT
				: rng.nextInt(DimensionType.MIN_HEIGHT / 16, ((DimensionType.MAX_COLUMN_HEIGHT + 1 - height) / 16) + 1) * 16;
		return new DimensionType(fixedTime, hasSkyLight, hasCeiling, ultrawarm, natural, coordinateScale, bedWorks, respawnAnchorWorks, minY, height, logicalHeight, infiniburn, effects, ambientLight, monsterSettings);
	}

	@Override
	public @NotNull IntProvider generateIntProvider(@NotNull OptionalLong seed, int min, int max) {
		Random rng = createRandom(seed);
		// don't forget max is inclusive :-)
		// TODO: support advanced IntProviders? (clamped, clampednormal, weightedlist)
		return switch (rng.nextInt(3)) {
			case 0 -> BiasedToBottomIntProvider.create(min, max);
			case 1 -> {
				int newMin = rng.nextInt(min, max);
				int newMax = newMin == max - 1 ? max : rng.nextInt(newMin + 1, max + 1);
				yield UniformIntProvider.create(newMin, newMax);
			}
			case 2 -> ConstantIntProvider.create(rng.nextInt(min, max + 1));
			default -> throw new AssertionError();
		};
	}

	@Override
	public @NotNull ChunkGenerator generateChunkGenerator(@NotNull OptionalLong seed) {
		return null; // TODO
	}

	@Override
	public @NotNull BiomeSource generateBiomeSource(@NotNull OptionalLong seed) {
		return null; // TODO
	}

	@Override
	public @NotNull Biome generateBiome(@NotNull OptionalLong seed) {
		Random rng = createRandom(seed);
		//// weather ////
		Biome.Builder biome = new Biome.Builder();
		biome.precipitation(randomFromEnum(rng, Biome.Precipitation.class));
		// anything above 1 is considered hot; adding 0.2 to even the odds for cold/hot sounds about right
		biome.temperature(rng.nextFloat(1.2f));
		if (rng.nextInt(10) == 0) biome.temperatureModifier(randomFromEnum(rng, Biome.TemperatureModifier.class));
		biome.downfall(rng.nextInt(101) / 100f);

		//// biome effects ////
		BiomeEffects.Builder effects = new BiomeEffects.Builder();
		effects.fogColor(rng.nextInt(0x1000000));
		effects.waterColor(rng.nextInt(0x1000000));
		effects.waterFogColor(rng.nextInt(0x1000000));
		effects.skyColor(rng.nextInt(0x1000000));
		if (rng.nextBoolean()) effects.foliageColor(rng.nextInt(0x1000000));
		if (rng.nextBoolean()) effects.grassColor(rng.nextInt(0x1000000));
		if (rng.nextInt(10) == 0) effects.grassColorModifier(randomFromEnum(rng, BiomeEffects.GrassColorModifier.class));
		if (rng.nextInt(10) == 0) {
			// biome particle config
			List<ParticleEffect> particles = Registries.PARTICLE_TYPE.stream()
					.filter(type -> type instanceof ParticleEffect)
					.map(type -> (ParticleEffect) type)
					.toList();
			ParticleEffect particle = randomFromList(rng, particles);
			float probability = rng.nextFloat();
			effects.particleConfig(new BiomeParticleConfig(particle, probability));
		}
		if (rng.nextInt(10) == 0) effects.loopSound(randomFromIndexedIterable(rng, Registries.SOUND_EVENT.getIndexedEntries()));
		if (rng.nextInt(10) == 0) {
			// biome mood sound
			RegistryEntry<SoundEvent> sound = randomFromIndexedIterable(rng, Registries.SOUND_EVENT.getIndexedEntries());
			int cultivationTicks = (int) (300 / (rng.nextDouble() + Double.MIN_VALUE));
			int spawnRange = (int) (1 / (rng.nextDouble() + Double.MIN_VALUE));
			double extraDistance = 1 / (rng.nextDouble() + Double.MIN_VALUE);
			effects.moodSound(new BiomeMoodSound(sound, cultivationTicks, spawnRange, extraDistance));
		}
		if (rng.nextInt(10) == 0) {
			// biome additions sound
			RegistryEntry<SoundEvent> sound = randomFromIndexedIterable(rng, Registries.SOUND_EVENT.getIndexedEntries());
			double chance = 0.005d / (rng.nextDouble() + Double.MIN_VALUE); // vanilla uses 0.0111d
			effects.additionsSound(new BiomeAdditionsSound(sound, chance));
		}
		if (rng.nextInt(10) == 0) {
			// music sound
			RegistryEntry<SoundEvent> sound = randomFromIndexedIterable(rng, Registries.SOUND_EVENT.getIndexedEntries());
			int minDelay = (int) (20 / (rng.nextDouble() + Double.MIN_VALUE));
			int maxDelay = (int) (minDelay * (2 / (rng.nextDouble() + Double.MIN_VALUE)));
			boolean replaceCurrentMusic = rng.nextBoolean();
			effects.music(new MusicSound(sound, minDelay, maxDelay, replaceCurrentMusic));
		}
		biome.effects(effects.build());

		//// spawn settings ////
		SpawnSettings.Builder spawns = new SpawnSettings.Builder();
		List<EntityType<?>> spawnableMobs = Registries.ENTITY_TYPE.stream()
				.filter(type -> type.getSpawnGroup() != SpawnGroup.MISC) // TODO: whitelist some misc mobs like armor stands
				.toList();
		Map<Identifier, EntityType<?>> allMobsToSpawn = new HashMap<>();
		for (SpawnGroup group : SpawnGroup.values()) {
			if (group == SpawnGroup.MISC) continue;
			List<EntityType<?>> toSpawn = randomSubset(rng, spawnableMobs, (int) (0.05d / (rng.nextDouble() + Double.MIN_VALUE)));
			for (EntityType<?> entity : toSpawn) {
				int weight = (int) (1 / (rng.nextDouble() + Double.MIN_VALUE));
				int minGroupSize = (int) (1 / (rng.nextDouble() + Double.MIN_VALUE));
				int maxGroupSize = (int) (minGroupSize * (1.5d / (rng.nextDouble() + Double.MIN_VALUE)));
				spawns.spawn(group, new SpawnSettings.SpawnEntry(entity, weight, minGroupSize, maxGroupSize));
				allMobsToSpawn.put(EntityType.getId(entity), entity);
			}
		}
		for (EntityType<?> entity : allMobsToSpawn.values()) {
			// spawn cost
			if (rng.nextInt(10) != 0) continue;
			double mass = rng.nextDouble();
			double gravityLimit = rng.nextDouble();
			spawns.spawnCost(entity, mass, gravityLimit);
		}
		spawns.creatureSpawnProbability((0.1f / (rng.nextFloat() + Float.MIN_VALUE)) - 0.09f); // vanilla uses 0.1f
		biome.spawnSettings(spawns.build());

		//// generation settings ////
		GenerationSettings.Builder generation = new GenerationSettings.Builder();
		RegistryEntryLookup<PlacedFeature> featureLookup = RegistryKeys.PLACED_FEATURE;
		biome.generationSettings(generation.build());



		return biome.build();
	}
}