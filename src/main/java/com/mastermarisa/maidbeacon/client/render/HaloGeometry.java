package com.mastermarisa.maidbeacon.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;


public final class HaloGeometry {
    public record ColorKey(double angle, int argb) {
    }

    private record Point(double sin, double cos, int argb) {
    }

    public static void fullRing(VertexConsumer consumer, PoseStack.Pose pose, float radius, float width,
                                float height, float stepDegrees, List<ColorKey> colors) {
        sweep(consumer, pose, radius, width, height, stepDegrees, 0.0D, Mth.TWO_PI, closeLoop(colors));
    }

    public static void arc(VertexConsumer consumer, PoseStack.Pose pose, float radius, float width,
                           float height, float stepDegrees, List<ColorKey> colors) {
        if (colors.isEmpty()) {
            return;
        }
        sweep(consumer, pose, radius, width, height, stepDegrees,
                colors.getFirst().angle(), colors.getLast().angle(), colors);
    }

    private static void sweep(VertexConsumer consumer, PoseStack.Pose pose, float radius, float width,
                              float height, float stepDegrees, double from, double to, List<ColorKey> colors) {
        double span = to - from;
        if (span <= 1.0e-6 || stepDegrees <= 0.0F) {
            return;
        }

        int segments = Math.max(1, (int) Math.ceil(Math.toDegrees(span) / stepDegrees));
        List<Point> points = new ArrayList<>(segments + 1);
        for (int i = 0; i <= segments; i++) {
            double angle = from + span * i / segments;
            points.add(new Point(Math.sin(angle), Math.cos(angle), colorAt(colors, angle)));
        }

        double radiusInner = radius - width / 2.0D;
        double radiusOuter = radius + width / 2.0D;
        for (int i = 0; i < points.size() - 1; i++) {
            Point start = points.get(i);
            Point end = points.get(i + 1);
            quad(consumer, pose, start, end, radiusInner, radiusOuter, 0.0F, 0.0F);
            quad(consumer, pose, start, end, radiusOuter, radius, 0.0F, height);
            quad(consumer, pose, start, end, radius, radiusInner, height, 0.0F);
        }
    }

    private static void quad(VertexConsumer consumer, PoseStack.Pose pose, Point start, Point end,
                             double innerRadius, double outerRadius, float innerY, float outerY) {
        vertex(consumer, pose, start, innerRadius, innerY);
        vertex(consumer, pose, start, outerRadius, outerY);
        vertex(consumer, pose, end, outerRadius, outerY);
        vertex(consumer, pose, end, innerRadius, innerY);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, Point point, double radius, float y) {
        consumer.addVertex(pose,
                        (float) (point.cos() * radius),
                        y,
                        (float) (point.sin() * radius))
                .setColor(point.argb())
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    private static int colorAt(List<ColorKey> colors, double angle) {
        if (colors.size() == 1) {
            return colors.getFirst().argb();
        }
        for (int i = 0; i < colors.size() - 1; i++) {
            ColorKey current = colors.get(i);
            ColorKey next = colors.get(i + 1);
            if (angle <= next.angle()) {
                double span = next.angle() - current.angle();
                float t = span <= 0.0D ? 0.0F : (float) ((angle - current.angle()) / span);
                return lerp(current.argb(), next.argb(), Mth.clamp(t, 0.0F, 1.0F));
            }
        }
        return colors.getLast().argb();
    }

    private static int lerp(int from, int to, float t) {
        int a = (int) Mth.lerp(t, (from >>> 24) & 0xFF, (to >>> 24) & 0xFF);
        int r = (int) Mth.lerp(t, (from >>> 16) & 0xFF, (to >>> 16) & 0xFF);
        int g = (int) Mth.lerp(t, (from >>> 8) & 0xFF, (to >>> 8) & 0xFF);
        int b = (int) Mth.lerp(t, from & 0xFF, to & 0xFF);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static List<ColorKey> closeLoop(List<ColorKey> colors) {
        if (colors.isEmpty()) {
            return colors;
        }
        if (colors.getLast().angle() >= Mth.TWO_PI - 1.0e-4) {
            return colors;
        }
        List<ColorKey> closed = new ArrayList<>(colors);
        closed.add(new ColorKey(Mth.TWO_PI, colors.getFirst().argb()));
        return closed;
    }

    private HaloGeometry() {
    }
}

