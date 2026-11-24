package com.mastermarisa.maidbeacon.logic;

import com.mastermarisa.maidbeacon.api.TierDef;
import com.mastermarisa.maidbeacon.content.BeaconComponent;
import com.mastermarisa.maidbeacon.init.ModItems;
import com.mastermarisa.maidbeacon.utils.MaidUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public final class BeaconService {
    public static boolean toggle(ServerPlayer player, InteractionHand hand, ResourceLocation auraId) {
        ItemStack beacon = player.getItemInHand(hand);
        if (!beacon.is(ModItems.MOBILE_BEACON.get())) {
            return false;
        }

        RegistryAccess access = player.serverLevel().registryAccess();
        BeaconState state = stateOf(beacon);
        BeaconState next;
        if (state.has(auraId)) {
            if (!BeaconRules.canDeactivate(state, auraId)) {
                return false;
            }
            next = state.withoutAura(auraId);
        } else {
            if (!BeaconRules.canActivate(access, state, auraId)) {
                return false;
            }
            next = state.withAura(auraId);
        }

        if (next.equals(state)) {
            return false;
        }
        beacon.set(BeaconComponent.BEACON_STATE.get(), next);
        return true;
    }

    public static boolean upgrade(ServerPlayer player, InteractionHand hand) {
        ItemStack beacon = player.getItemInHand(hand);
        if (!beacon.is(ModItems.MOBILE_BEACON.get())) {
            return false;
        }

        RegistryAccess access = player.serverLevel().registryAccess();
        BeaconState state = stateOf(beacon);
        TierDef.Upgrade upgrade = BeaconIndex.tier(access, state.level())
                .flatMap(TierDef::upgrade)
                .orElse(null);
        if (upgrade == null) {
            return false;
        }

        IItemHandler inventory = player.getCapability(Capabilities.ItemHandler.ENTITY);
        if (inventory == null) {
            return false;
        }
        Ingredient material = upgrade.material();
        if (MaidUtils.count(inventory, material) < upgrade.count()) {
            return false;
        }
        if (MaidUtils.tryExtract(inventory, upgrade.count(), material, true).isEmpty()) {
            return false;
        }

        beacon.set(BeaconComponent.BEACON_STATE.get(), state.withLevel(state.level() + 1));
        return true;
    }

    public static BeaconState stateOf(ItemStack stack) {
        return stack.getOrDefault(BeaconComponent.BEACON_STATE.get(), BeaconState.EMPTY);
    }

    private BeaconService() {
    }
}

