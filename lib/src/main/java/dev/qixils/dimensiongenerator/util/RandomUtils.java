package dev.qixils.dimensiongenerator.util;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.collection.IndexedIterable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static dev.qixils.dimensiongenerator.util.RegistryUtils.getRegisteredValue;
import static dev.qixils.dimensiongenerator.util.RegistryUtils.tagsFor;

public class RandomUtils {
    private RandomUtils() {
    }

    @SafeVarargs
    public static <T> T randomFromArray(Random random, T... array) {
        return array[random.nextInt(array.length)];
    }

    public static <T> T randomFromList(Random random, List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    public static <T> T randomFromCollection(Random random, Collection<T> collection) {
        return randomFromList(random, new ArrayList<>(collection));
    }

    public static <T> T randomFromIterator(Random random, Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext())
            list.add(iterator.next());
        return randomFromList(random, list);
    }

    public static <T> T randomFromIterable(Random random, Iterable<T> iterable) {
        return randomFromIterator(random, iterable.iterator());
    }

    public static <T> T randomFromIndexedIterable(Random random, IndexedIterable<T> iterable) {
        return iterable.get(random.nextInt(iterable.size()));
    }

    public static <T> T randomRegisteredValue(Random random, Registry<T> registry) {
        return randomFromIndexedIterable(random, registry);
    }

    public static <T> T randomRegisteredValue(Random random, RegistryKey<Registry<T>> registryKey) {
        return randomRegisteredValue(random, getRegisteredValue(registryKey));
    }

    public static <T> TagKey<T> randomTagFor(Random random, RegistryKey<Registry<T>> registryKey) {
        List<TagKey<T>> tags = new ArrayList<>(tagsFor(registryKey));
        return tags.get(random.nextInt(tags.size()));
    }

    public static <T> T randomFromStream(Random random, Stream<T> stream) {
        return randomFromList(random, stream.toList());
    }

    public static <T extends Enum<T>> T randomFromEnum(Random random, Class<T> enumClass) {
        return randomFromArray(random, enumClass.getEnumConstants());
    }
}
