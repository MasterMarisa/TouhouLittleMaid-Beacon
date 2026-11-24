package com.mastermarisa.maidbeacon.network;

import com.mastermarisa.maidbeacon.MaidBeacon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record UpgradePayload(boolean offHand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpgradePayload> TYPE =
            new CustomPacketPayload.Type<>(MaidBeacon.resourceLocation("upgrade_beacon"));

    public static final StreamCodec<FriendlyByteBuf, UpgradePayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeBoolean(payload.offHand()),
            buf -> new UpgradePayload(buf.readBoolean()));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

