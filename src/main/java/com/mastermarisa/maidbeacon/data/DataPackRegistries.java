package com.mastermarisa.maidbeacon.data;

import com.mastermarisa.maidbeacon.MaidBeacon;
import com.mastermarisa.maidbeacon.api.AuraDef;
import com.mastermarisa.maidbeacon.api.MaidBeaconRegistries;
import com.mastermarisa.maidbeacon.api.TierDef;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class DataPackRegistries {
    public static void register(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(MaidBeaconRegistries.AURA, AuraDef.CODEC, AuraDef.CODEC);
        event.dataPackRegistry(MaidBeaconRegistries.TIER, TierDef.CODEC, TierDef.CODEC);
    }

    private DataPackRegistries() {
    }
}

