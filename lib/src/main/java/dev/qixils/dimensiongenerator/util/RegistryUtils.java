package dev.qixils.dimensiongenerator.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.HashSet;
import java.util.Set;

public class RegistryUtils {
    private RegistryUtils() {
    }

    public static final Set<TagKey<?>> TAG_KEYS = new HashSet<>();

    @SuppressWarnings({"unchecked", "DataFlowIssue", "rawtypes"})
    public static <T> T getRegisteredValue(RegistryKey<T> key) {
        // the root registry does not seem to contain itself so we need a special case for it
        Registry registry = key.getRegistry().equals(Registries.ROOT_KEY)
                ? Registries.REGISTRIES
                : Registries.REGISTRIES.get(key.getRegistry());
        return (T) registry.get(key.getValue());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Set<TagKey<T>> tagsFor(RegistryKey<Registry<T>> registryKey) {
        Set<TagKey<T>> tags = new HashSet<>();
        for (TagKey tagKey : TAG_KEYS) {
            if (tagKey.registry().equals(registryKey)) { // could use TagKey#isOf but IJ whines about type errors
                tags.add(tagKey);
            }
        }
        return tags;
    }
}
