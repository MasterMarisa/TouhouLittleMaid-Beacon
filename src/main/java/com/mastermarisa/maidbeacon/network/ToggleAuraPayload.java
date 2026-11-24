package com.mastermarisa.maidbeacon.network;

import com.mastermarisa.maidbeacon.MaidBeacon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ToggleAuraPayload(boolean offHand, ResourceLocation auraId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleAuraPayload> TYPE =
            new CustomPacketPayload.Type<>(MaidBeacon.resourceLocation("toggle_aura"));

    public static final StreamCodec<FriendlyByteBuf, ToggleAuraPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ToggleAuraPayload::offHand,
            ResourceLocation.STREAM_CODEC, ToggleAuraPayload::auraId,
            ToggleAuraPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

