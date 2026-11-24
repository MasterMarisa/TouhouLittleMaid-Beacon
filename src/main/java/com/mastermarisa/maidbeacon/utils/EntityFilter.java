package com.mastermarisa.maidbeacon.utils;

import com.mastermarisa.maidbeacon.config.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class EntityFilter {
    private record Rules(Set<EntityType<?>> types, List<TagKey<EntityType<?>>> tags) {
    }

    private static volatile Rules rules;

    public static boolean filter(EntityType<?> type) {
        Rules current = rules();
        if (current.types().contains(type)) {
            return true;
        }
        for (TagKey<EntityType<?>> tag : current.tags()) {
            if (type.is(tag)) {
                return true;
            }
        }
        return false;
    }

    public static void invalidate() {
        rules = null;
    }

    public static void onConfigReloaded(net.neoforged.fml.event.config.ModConfigEvent.Reloading event) {
        invalidate();
    }

    private static Rules rules() {
        Rules current = rules;
        if (current == null) {
            current = parse();
            rules = current;
        }
        return current;
    }

    private static Rules parse() {
        Set<EntityType<?>> types = new HashSet<>();
        List<TagKey<EntityType<?>>> tags = new ArrayList<>();
        for (String raw : Config.whitelist()) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            if (raw.startsWith("#")) {
                ResourceLocation location = ResourceLocation.tryParse(raw.substring(1));
                if (location != null) {
                    tags.add(TagKey.create(Registries.ENTITY_TYPE, location));
                }
            } else {
                ResourceLocation location = ResourceLocation.tryParse(raw);
                if (location != null) {
                    EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(location);
                    types.add(type);
                }
            }
        }
        return new Rules(types, List.copyOf(tags));
    }

    private EntityFilter() {
    }
}

