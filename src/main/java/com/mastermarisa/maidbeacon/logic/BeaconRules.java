package com.mastermarisa.maidbeacon.logic;

import com.mastermarisa.maidbeacon.api.AuraDef;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

public final class BeaconRules {
    public static int usedCost(RegistryAccess access, BeaconState state) {
        int total = 0;
        for (ResourceLocation id : state.auras()) {
            total += BeaconIndex.aura(access, id).map(AuraDef::cost).orElse(0);
        }
        return total;
    }

    public static int budget(RegistryAccess access, BeaconState state) {
        return BeaconIndex.budget(access, state.level());
    }

    public static int remainingBudget(RegistryAccess access, BeaconState state) {
        return budget(access, state) - usedCost(access, state);
    }

    public static boolean canActivate(RegistryAccess access, BeaconState state, ResourceLocation auraId) {
        AuraDef aura = BeaconIndex.aura(access, auraId).orElse(null);
        if (aura == null) {
            return false;
        }
        if (aura.tier() > state.level()) {
            return false;
        }
        return usedCost(access, state) + aura.cost() <= budget(access, state);
    }

    public static boolean canDeactivate(BeaconState state, ResourceLocation auraId) {
        return state.has(auraId);
    }

    public static boolean canUpgrade(RegistryAccess access, BeaconState state) {
        return BeaconIndex.tier(access, state.level()).map(tier -> !tier.isMaxLevel()).orElse(false);
    }

    private BeaconRules() {
    }
}

