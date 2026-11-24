package com.mastermarisa.maidbeacon.network;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mastermarisa.maidbeacon.MaidBeacon;
import com.mastermarisa.maidbeacon.content.RenderSettingsAttachment;
import com.mastermarisa.maidbeacon.data.ExtraRenderSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncRenderSettingsPayload(int entityId, ExtraRenderSettings settings) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SyncRenderSettingsPayload> TYPE =
            new CustomPacketPayload.Type<>(MaidBeacon.resourceLocation("sync_render_settings"));

    public static final StreamCodec<FriendlyByteBuf, SyncRenderSettingsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.entityId());
                ExtraRenderSettings.STREAM_CODEC.encode(buf, payload.settings());
            },
            buf -> new SyncRenderSettingsPayload(buf.readVarInt(), ExtraRenderSettings.STREAM_CODEC.decode(buf)));

    private static final double MAX_DISTANCE_SQUARED = 32.0D * 32.0D;

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncRenderSettingsPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        ServerLevel level = player.serverLevel();
        if (!(level.getEntity(payload.entityId()) instanceof EntityMaid maid)) {
            return;
        }
        if (!mayEdit(player, maid)) {
            MaidBeacon.LOGGER.debug("Rejected render settings update for maid {} from {}",
                    payload.entityId(), player.getGameProfile().getName());
            return;
        }
        maid.setData(RenderSettingsAttachment.TYPE.get(), payload.settings());
    }

    private static boolean mayEdit(ServerPlayer player, EntityMaid maid) {
        if (player.distanceToSqr(maid) > MAX_DISTANCE_SQUARED) {
            return false;
        }
        LivingEntity owner = maid.getOwner();
        return owner == null || owner.getUUID().equals(player.getUUID()) || player.isCreative();
    }
}

