package com.mastermarisa.maidbeacon;

import com.mastermarisa.maidbeacon.config.Config;
import com.mastermarisa.maidbeacon.init.ModDataComponents;
import com.mastermarisa.maidbeacon.init.ModEntities;
import com.mastermarisa.maidbeacon.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MaidBeacon.MOD_ID)
public class MaidBeacon {
    public static final String MOD_ID = "maidbeacon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ResourceLocation resourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public MaidBeacon(IEventBus modEventBus, ModContainer modContainer) {
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModDataComponents.register(modEventBus);
        Config.register(modContainer);
    }
}
