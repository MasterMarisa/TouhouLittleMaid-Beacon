package com.mastermarisa.maidbeacon.config;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class Config {
    public static int checkInterval() {
        return Server.EFFECT_CHECK_INTERVAL.getAsInt();
    }

    public static List<? extends String> whitelist() {
        return Server.TARGET_WHITELIST.get();
    }

    public static double renderYOffset() {
        return Client.RENDER_Y_OFFSET.getAsDouble();
    }

    public static void register(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, Server.SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerConfig(ModConfig.Type.CLIENT, Client.SPEC);
        }
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

    private static final class Client {
        static final ModConfigSpec.DoubleValue RENDER_Y_OFFSET;
        static final ModConfigSpec SPEC;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

            RENDER_Y_OFFSET = builder
                    .translation("config.maidbeacon.client.render_y_offset")
                    .comment("Vertical offset in blocks applied to the beacon beam, model and halos.",
                            "Raise it for maid models that are taller than the vanilla hitbox,",
                            "lower it for smaller ones.")
                    .defineInRange("render_y_offset", 0.0D, -2.0D, 4.0D);

            SPEC = builder.build();
        }
    }

    private Config() {
    }
}
