package com.mastermarisa.maidbeacon.logic;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mastermarisa.maidbeacon.api.AuraDef;
import com.mastermarisa.maidbeacon.config.Config;
import com.mastermarisa.maidbeacon.content.BeaconComponent;
import com.mastermarisa.maidbeacon.utils.EntityFilter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AuraApplier {
    public static void apply(ServerLevel level, EntityMaid maid, ItemStack beacon) {
        BeaconState state = BeaconService.stateOf(beacon);
        if (state.auras().isEmpty()) {
            return;
        }

        RegistryAccess access = level.registryAccess();
        Map<Float, List<AuraDef>> byRange = new HashMap<>();
        for (ResourceLocation id : state.auras()) {
            BeaconIndex.aura(access, id).ifPresent(aura ->
                    byRange.computeIfAbsent(aura.range(), key -> new ArrayList<>(2)).add(aura));
        }
        if (byRange.isEmpty()) {
            return;
        }

        int duration = Math.min(Config.checkInterval() + 10, 60);

        for (Map.Entry<Float, List<AuraDef>> entry : byRange.entrySet()) {
            float range = entry.getKey();
            double rangeSquared = (double) range * (double) range;
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, maid.getBoundingBox().inflate(range))) {
                if (!EntityFilter.filter(target.getType())) {
                    continue;
                }
                if (target.distanceToSqr(maid) > rangeSquared) {
                    continue;
                }
                for (AuraDef aura : entry.getValue()) {
                    target.addEffect(new MobEffectInstance(aura.effect(), duration, aura.amplifier(), false, true));
                }
            }
        }
    }

    private AuraApplier() {
    }
}

