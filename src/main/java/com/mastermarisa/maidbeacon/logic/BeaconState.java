package com.mastermarisa.maidbeacon.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record BeaconState(int level, List<ResourceLocation> auras) {
    private static final Comparator<ResourceLocation> ID_ORDER = Comparator.comparing(ResourceLocation::toString);

    public static final BeaconState EMPTY = new BeaconState(0, List.of());

    public BeaconState {
        auras = auras.stream().distinct().sorted(ID_ORDER).toList();
    }

    public static final Codec<BeaconState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("level", 0).forGetter(BeaconState::level),
            ResourceLocation.CODEC.listOf().optionalFieldOf("auras", List.of()).forGetter(BeaconState::auras)
    ).apply(instance, BeaconState::new));

    public static final StreamCodec<ByteBuf, BeaconState> STREAM_CODEC = StreamCodec.of(
            (buf, state) -> {
                VarInt.write(buf, state.level);
                VarInt.write(buf, state.auras.size());
                for (ResourceLocation aura : state.auras) {
                    ResourceLocation.STREAM_CODEC.encode(buf, aura);
                }
            },
            buf -> {
                int level = VarInt.read(buf);
                int size = VarInt.read(buf);
                List<ResourceLocation> auras = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    auras.add(ResourceLocation.STREAM_CODEC.decode(buf));
                }
                return new BeaconState(level, auras);
            });

    public boolean has(ResourceLocation aura) {
        return auras.contains(aura);
    }

    public BeaconState withAura(ResourceLocation aura) {
        if (has(aura)) {
            return this;
        }
        List<ResourceLocation> next = new ArrayList<>(auras);
        next.add(aura);
        return new BeaconState(level, next);
    }

    public BeaconState withoutAura(ResourceLocation aura) {
        if (!has(aura)) {
            return this;
        }
        return new BeaconState(level, auras.stream().filter(id -> !id.equals(aura)).toList());
    }

    public BeaconState withLevel(int newLevel) {
        return new BeaconState(newLevel, auras);
    }
}

