package com.mastermarisa.maidbeacon.api;

import com.mastermarisa.maidbeacon.MaidBeacon;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class MaidBeaconRegistries {
    public static final ResourceKey<Registry<AuraDef>> AURA =
            ResourceKey.createRegistryKey(MaidBeacon.resourceLocation("aura"));

    public static final ResourceKey<Registry<TierDef>> TIER =
            ResourceKey.createRegistryKey(MaidBeacon.resourceLocation("tier"));

    private MaidBeaconRegistries() {
    }
}

