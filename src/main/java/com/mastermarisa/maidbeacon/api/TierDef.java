package com.mastermarisa.maidbeacon.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public record TierDef(int level, int budget, Optional<Upgrade> upgrade) {
    public record Upgrade(Ingredient material, int count) {
        public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("material").forGetter(Upgrade::material),
                Codec.intRange(1, 576).fieldOf("count").forGetter(Upgrade::count)
        ).apply(instance, Upgrade::new));
    }

    public static final Codec<TierDef> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 1024).fieldOf("level").forGetter(TierDef::level),
            Codec.intRange(0, 1024).fieldOf("budget").forGetter(TierDef::budget),
            Upgrade.CODEC.optionalFieldOf("upgrade").forGetter(TierDef::upgrade)
    ).apply(instance, TierDef::new));

    public boolean isMaxLevel() {
        return upgrade.isEmpty();
    }
}

