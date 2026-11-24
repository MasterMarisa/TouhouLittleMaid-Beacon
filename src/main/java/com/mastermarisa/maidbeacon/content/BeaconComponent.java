package com.mastermarisa.maidbeacon.content;

import com.mastermarisa.maidbeacon.MaidBeacon;
import com.mastermarisa.maidbeacon.logic.BeaconState;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BeaconComponent {
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MaidBeacon.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BeaconState>> BEACON_STATE =
            COMPONENTS.register("mobile_beacon_data", () -> DataComponentType.<BeaconState>builder()
                    .persistent(BeaconState.CODEC)
                    .networkSynchronized(BeaconState.STREAM_CODEC)
                    .build());

    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }

    private BeaconComponent() {
    }
}

