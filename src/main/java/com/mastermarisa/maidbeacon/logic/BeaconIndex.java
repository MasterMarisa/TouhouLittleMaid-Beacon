package com.mastermarisa.maidbeacon.logic;

import com.mastermarisa.maidbeacon.api.AuraDef;
import com.mastermarisa.maidbeacon.api.MaidBeaconRegistries;
import com.mastermarisa.maidbeacon.api.TierDef;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class BeaconIndex {
    public record Entry(ResourceLocation id, AuraDef def) {
    }

    private static final Comparator<Entry> ENTRY_ORDER = Comparator
            .comparingInt((Entry entry) -> entry.def().tier())
            .thenComparing(entry -> entry.id().toString());

    public static List<Entry> auraEntries(RegistryAccess access) {
        return registry(access, MaidBeaconRegistries.AURA)
                .map(reg -> reg.entrySet().stream()
                        .map(entry -> new Entry(entry.getKey().location(), entry.getValue()))
                        .sorted(ENTRY_ORDER)
                        .toList())
                .orElse(List.of());
    }

    public static Optional<AuraDef> aura(RegistryAccess access, ResourceLocation id) {
        return registry(access, MaidBeaconRegistries.AURA).flatMap(reg -> reg.getOptional(id));
    }

    public static List<TierDef> tiers(RegistryAccess access) {
        return registry(access, MaidBeaconRegistries.TIER)
                .map(reg -> reg.stream().sorted(Comparator.comparingInt(TierDef::level)).toList())
                .orElse(List.of());
    }

    public static Optional<TierDef> tier(RegistryAccess access, int level) {
        return tiers(access).stream().filter(tier -> tier.level() == level).findFirst();
    }

    public static int budget(RegistryAccess access, int level) {
        return tier(access, level).map(TierDef::budget).orElse(0);
    }

    public static int maxLevel(RegistryAccess access) {
        return tiers(access).stream().mapToInt(TierDef::level).max().orElse(0);
    }

    private static <T> Optional<Registry<T>> registry(RegistryAccess access, ResourceKey<Registry<T>> key) {
        return access.registry(key);
    }

    private BeaconIndex() {
    }
}

