package com.mastermarisa.maidbeacon.event;

import com.mastermarisa.maidbeacon.MaidBeacon;
import com.mastermarisa.maidbeacon.api.MaidBeaconRegistries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = MaidBeacon.MOD_ID)
public final class RegistryLoadReport {
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        var registries = event.getServer().registryAccess();
        int auras = registries.registryOrThrow(MaidBeaconRegistries.AURA).size();
        int tiers = registries.registryOrThrow(MaidBeaconRegistries.TIER).size();
        MaidBeacon.LOGGER.info("Datapack content loaded: {} auras, {} tiers", auras, tiers);
        if (auras == 0 || tiers == 0) {
            MaidBeacon.LOGGER.warn("""
                    No aura/tier definitions were loaded. Expected them under
                    data/<datapack>/maidbeacon/aura/*.json and data/<datapack>/maidbeacon/tier/*.json""");
        }
    }

    private RegistryLoadReport() {
    }
}

