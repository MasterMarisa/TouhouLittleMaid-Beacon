package com.mastermarisa.maidbeacon.client.render;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mastermarisa.maidbeacon.MaidBeacon;
import com.mastermarisa.maidbeacon.client.render.HaloGeometry.ColorKey;
import com.mastermarisa.maidbeacon.content.RenderSettingsAttachment;
import com.mastermarisa.maidbeacon.data.ExtraRenderSettings;
import com.mastermarisa.maidbeacon.utils.MaidUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;

import java.awt.Color;
import java.util.List;

@EventBusSubscriber(modid = MaidBeacon.MOD_ID, value = Dist.CLIENT)
public final class MaidBeaconRenderEvents {
    private static final ResourceLocation BEAM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/beacon_beam.png");

    private static final int BEAM_COLOR = Color.WHITE.getRGB();
    private static final int HALO_WHITE = new Color(1, 1, 1, 0.6F).getRGB();
    private static final int HALO_BLUE = new Color(181, 221, 237, 10).getRGB();

    private static final double MAX_DISTANCE_SQUARED = 256.0D * 256.0D;
    private static final double LOD_DISTANCE_SQUARED = 64.0D * 64.0D;
    private static final float FINE_STEP_DEGREES = 2.0F;
    private static final float COARSE_STEP_DEGREES = 6.0F;

    private static final List<ColorKey> THREE_KEY_WHITE_FIRST = List.of(
            new ColorKey(0.0D, HALO_WHITE),
            new ColorKey(Math.PI, HALO_BLUE),
            new ColorKey(Math.toRadians(362.0D), HALO_BLUE));
    private static final List<ColorKey> THREE_KEY_BLUE_FIRST = List.of(
            new ColorKey(0.0D, HALO_BLUE),
            new ColorKey(Math.PI, HALO_BLUE),
            new ColorKey(Math.toRadians(362.0D), HALO_WHITE));
    private static final List<ColorKey> TWO_KEY_BLUE_FIRST = List.of(
            new ColorKey(0.0D, HALO_BLUE),
            new ColorKey(Math.toRadians(122.0D), HALO_WHITE));

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        long gameTime = level.getGameTime();

        boolean drewAnything = false;
        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof EntityMaid maid) || maid.isRemoved()) {
                continue;
            }
            double distanceSquared = maid.position().distanceToSqr(camera);
            if (distanceSquared > MAX_DISTANCE_SQUARED) {
                continue;
            }
            ExtraRenderSettings settings = maid.getData(RenderSettingsAttachment.TYPE.get());
            if (!settings.enableRendering) {
                continue;
            }
            ItemStack beacon = MaidUtils.getEquippedMobileBeacon(maid);
            if (beacon.isEmpty()) {
                continue;
            }

            poseStack.pushPose();
            poseStack.translate(
                    Mth.lerp(partialTick, maid.xOld, maid.getX()) - camera.x,
                    Mth.lerp(partialTick, maid.yOld, maid.getY()) - camera.y,
                    Mth.lerp(partialTick, maid.zOld, maid.getZ()) - camera.z);
            drawBeacon(poseStack, buffers, settings, maid, partialTick, gameTime,
                    distanceSquared > LOD_DISTANCE_SQUARED ? COARSE_STEP_DEGREES : FINE_STEP_DEGREES);
            poseStack.popPose();
            drewAnything = true;
        }

        if (drewAnything) {
            buffers.endBatch();
        }
    }

    private static void drawBeacon(PoseStack poseStack, MultiBufferSource buffers, ExtraRenderSettings settings,
                                   EntityMaid maid, float partialTick, long gameTime, float haloStep) {
        if (settings.enableBeaconBeam) {
            BeaconRenderer.renderBeaconBeam(poseStack, buffers, partialTick, 1.0F, gameTime,
                    1, 256, BEAM_COLOR, 0.06F, 0.06F, null, maid);
        }

        if (settings.enableBeaconModel) {
            BeaconRenderer.renderBeaconModel(poseStack, buffers, 0xF000F0, maid, partialTick);
        }

        if (!settings.enableHeadHalo && !settings.enableSkyHalo) {
            return;
        }

        VertexConsumer halo = buffers.getBuffer(RenderType.entityTranslucentEmissive(BEAM_TEXTURE));
        if (settings.enableHeadHalo) {
            headHalo(halo, poseStack, maid, gameTime, haloStep);
        }
        if (settings.enableSkyHalo) {
            skyHalos(halo, poseStack, gameTime, haloStep);
        }
    }

    private static void headHalo(VertexConsumer consumer, PoseStack poseStack, EntityMaid maid,
                                 long gameTime, float step) {
        poseStack.pushPose();
        poseStack.translate(0.0D, maid.getBbHeight() + 0.35D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees((gameTime % 240L) * 1.5F));
        HaloGeometry.fullRing(consumer, poseStack.last(), 0.34F, 0.05F, 0.03F, step,
                List.of(new ColorKey(0.0D, HALO_WHITE)));
        poseStack.popPose();
    }

    private static void skyHalos(VertexConsumer consumer, PoseStack poseStack, long gameTime, float step) {
        rotatingArc(consumer, poseStack, gameTime + 170L, 3.0D, 38.0F, 0.5F, 0.33F, step,
                THREE_KEY_WHITE_FIRST, true, new Vector3f(0.0F, 97.0F, 0.0F));
        rotatingArc(consumer, poseStack, gameTime + 80L, 4.5D, 33.8F, 0.5F, 0.33F, step,
                TWO_KEY_BLUE_FIRST, false, new Vector3f(0.0F, 97.0F, 0.0F));
        rotatingArc(consumer, poseStack, gameTime, 1.5D, 63.5F, 0.5F, 0.33F, step,
                THREE_KEY_WHITE_FIRST, true, new Vector3f(0.0F, 98.0F, 0.0F));

        staticRing(consumer, poseStack, 74.5F, 0.4F, 0.25F, step, new Vector3f(0.0F, 97.0F, 0.0F));

        rotatingArc(consumer, poseStack, gameTime, 1.0D, 119.0F, 0.6F, 0.4F, step,
                THREE_KEY_BLUE_FIRST, false, new Vector3f(0.0F, 100.0F, 0.0F));

        double orbit = (gameTime % 1080L) / 1080.0D * 2.0D * Math.PI;
        staticRing(consumer, poseStack, 26.0F, 0.6F, 0.4F, step, new Vector3f(
                (float) (122.0D * Math.cos(orbit)), 99.0F, (float) (122.0D * Math.sin(orbit))));

        orbit = ((gameTime + 980L) % 1580L) / 1580.0D * 2.0D * Math.PI;
        staticRing(consumer, poseStack, 20.0F, 0.6F, 0.4F, step, new Vector3f(
                -(float) (129.0D * Math.cos(orbit)), 99.0F, -(float) (129.0D * Math.sin(orbit))));
    }

    private static void rotatingArc(VertexConsumer consumer, PoseStack poseStack, long gameTime, double speed,
                                    float radius, float width, float height, float step, List<ColorKey> colors,
                                    boolean clockwise, Vector3f offset) {
        double cycle = 360.0D / speed;
        double progress = (gameTime % (long) cycle) / cycle;
        float angle = (float) (2.0D * Math.PI * progress);

        poseStack.pushPose();
        poseStack.translate(offset.x(), offset.y(), offset.z());
        poseStack.mulPose(clockwise ? Axis.YP.rotation(angle) : Axis.YN.rotation(angle));
        HaloGeometry.arc(consumer, poseStack.last(), radius, width, height, step, colors);
        poseStack.popPose();
    }

    private static void staticRing(VertexConsumer consumer, PoseStack poseStack, float radius, float width,
                                   float height, float step, Vector3f offset) {
        poseStack.pushPose();
        poseStack.translate(offset.x(), offset.y(), offset.z());
        HaloGeometry.fullRing(consumer, poseStack.last(), radius, width, height, step,
                List.of(new ColorKey(0.0D, HALO_BLUE)));
        poseStack.popPose();
    }

    private MaidBeaconRenderEvents() {
    }
}

