package com.mastermarisa.maidbeacon.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public record AuraDef(Holder<MobEffect> effect, int amplifier, int tier, int cost, float range) {
    public static final Codec<AuraDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("effect").forGetter(AuraDef::effect),
            Codec.intRange(0, 255).fieldOf("amplifier").forGetter(AuraDef::amplifier),
            Codec.intRange(0, 1024).fieldOf("tier").forGetter(AuraDef::tier),
            Codec.intRange(0, 1024).fieldOf("cost").forGetter(AuraDef::cost),
            Codec.floatRange(0.5F, 256.0F).fieldOf("range").forGetter(AuraDef::range)
    ).apply(instance, AuraDef::new));

    public double rangeSquared() {
        return (double) range * (double) range;
    }
}

