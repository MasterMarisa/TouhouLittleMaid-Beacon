package com.mastermarisa.maidbeacon.content;

import com.mastermarisa.maidbeacon.MaidBeacon;
import com.mastermarisa.maidbeacon.data.ExtraRenderSettings;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class RenderSettingsAttachment {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MaidBeacon.MOD_ID);

    public static final Supplier<AttachmentType<ExtraRenderSettings>> TYPE = ATTACHMENTS.register(
            "extra_render_settings",
            () -> AttachmentType.serializable(ExtraRenderSettings::new)
                    .sync(new ExtraRenderSettings.Syncer())
                    .build());

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }

    private RenderSettingsAttachment() {
    }
}

