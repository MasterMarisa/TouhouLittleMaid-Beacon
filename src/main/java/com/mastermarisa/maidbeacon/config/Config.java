package com.mastermarisa.maidbeacon.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class Config {
    public static int checkInterval() {
        return Server.EFFECT_CHECK_INTERVAL.getAsInt();
    }

    public static List<? extends String> whitelist() {
        return Server.TARGET_WHITELIST.get();
    }

    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Server.SPEC);
    }

    private static final class Server {
        static final ModConfigSpec.IntValue EFFECT_CHECK_INTERVAL;
        static final ModConfigSpec.ConfigValue<List<? extends String>> TARGET_WHITELIST;
        static final ModConfigSpec SPEC;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            EFFECT_CHECK_INTERVAL = builder
                    .translation("config.maidbeacon.server.effect_check_interval")
                    .comment("Ticks between two aura applications. Only affects performance and how",
                            "quickly effects react when entities enter or leave the range.")
                    .defineInRange("effect_check_interval", 20, 1, Integer.MAX_VALUE);

            TARGET_WHITELIST = builder
                    .translation("config.maidbeacon.server.whitelist")
                    .comment("Entity types the beacon may affect. Plain registry names, or #tags.",
                            "The list is re-read when the config is reloaded.")
                    .defineList("target_whitelist",
                            List.of("minecraft:player", "touhou_little_maid:maid"),
                            () -> "",
                            value -> value instanceof String);

            SPEC = builder.build();
        }
    }

    private Config() {
    }
}

