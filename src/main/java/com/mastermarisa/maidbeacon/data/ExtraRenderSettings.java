package com.mastermarisa.maidbeacon.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class ExtraRenderSettings implements INBTSerializable<CompoundTag> {
    public boolean enableRendering = true;
    public boolean enableBeaconBeam = true;
    public boolean enableBeaconModel = true;
    public boolean enableHeadHalo = false;
    public boolean enableSkyHalo = false;
    public byte headHaloStyle = 0;
    public byte skyHaloStyle = 0;

    @Override
    public CompoundTag serializeNBT(@Nullable HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("enable_rendering", enableRendering);
        tag.putBoolean("enable_beacon_beam", enableBeaconBeam);
        tag.putBoolean("enable_beacon_model", enableBeaconModel);
        tag.putBoolean("enable_head_halo", enableHeadHalo);
        tag.putBoolean("enable_sky_halo", enableSkyHalo);
        tag.putByte("head_halo_style", headHaloStyle);
        tag.putByte("sky_halo_style", skyHaloStyle);
        return tag;
    }

    @Override
    public void deserializeNBT(@Nullable HolderLookup.Provider provider, CompoundTag tag) {
        enableRendering = tag.getBoolean("enable_rendering");
        enableBeaconBeam = tag.getBoolean("enable_beacon_beam");
        enableBeaconModel = tag.getBoolean("enable_beacon_model");
        enableHeadHalo = tag.getBoolean("enable_head_halo");
        enableSkyHalo = tag.getBoolean("enable_sky_halo");
        headHaloStyle = tag.getByte("head_halo_style");
        skyHaloStyle = tag.getByte("sky_halo_style");
    }

    public static final StreamCodec<ByteBuf, ExtraRenderSettings> STREAM_CODEC = StreamCodec.of(
            (buf, settings) -> {
                buf.writeBoolean(settings.enableRendering);
                buf.writeBoolean(settings.enableBeaconBeam);
                buf.writeBoolean(settings.enableBeaconModel);
                buf.writeBoolean(settings.enableHeadHalo);
                buf.writeBoolean(settings.enableSkyHalo);
                buf.writeByte(settings.headHaloStyle);
                buf.writeByte(settings.skyHaloStyle);
            },
            buf -> {
                ExtraRenderSettings settings = new ExtraRenderSettings();
                settings.enableRendering = buf.readBoolean();
                settings.enableBeaconBeam = buf.readBoolean();
                settings.enableBeaconModel = buf.readBoolean();
                settings.enableHeadHalo = buf.readBoolean();
                settings.enableSkyHalo = buf.readBoolean();
                settings.headHaloStyle = buf.readByte();
                settings.skyHaloStyle = buf.readByte();
                return settings;
            });

    public static class Syncer implements AttachmentSyncHandler<ExtraRenderSettings> {
        @Override
        public void write(RegistryFriendlyByteBuf buf, ExtraRenderSettings settings, boolean initialSync) {
            STREAM_CODEC.encode(buf, settings);
        }

        @Override
        public @Nullable ExtraRenderSettings read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf,
                                                  @Nullable ExtraRenderSettings existing) {
            return STREAM_CODEC.decode(buf);
        }
    }
}

