package com.mastermarisa.maidbeacon.network;

import com.mastermarisa.maidbeacon.MaidBeacon;
import com.mastermarisa.maidbeacon.logic.BeaconService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MaidBeacon.MOD_ID)
public final class NetworkHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ToggleAuraPayload.TYPE,
                ToggleAuraPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = serverPlayer(context.player());
                    if (player != null) {
                        BeaconService.toggle(player, hand(payload.offHand()), payload.auraId());
                    }
                }));

        registrar.playToServer(
                UpgradePayload.TYPE,
                UpgradePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    ServerPlayer player = serverPlayer(context.player());
                    if (player != null) {
                        BeaconService.upgrade(player, hand(payload.offHand()));
                    }
                }));

        registrar.playToServer(
                SyncRenderSettingsPayload.TYPE,
                SyncRenderSettingsPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> SyncRenderSettingsPayload.handle(payload, context)));
    }

    private static ServerPlayer serverPlayer(Player player) {
        return player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }

    private static InteractionHand hand(boolean offHand) {
        return offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    private NetworkHandler() {
    }
}

