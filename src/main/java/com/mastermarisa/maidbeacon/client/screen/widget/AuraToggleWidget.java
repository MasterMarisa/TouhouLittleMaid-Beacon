package com.mastermarisa.maidbeacon.client.screen.widget;

import com.mastermarisa.maidbeacon.api.AuraDef;
import com.mastermarisa.maidbeacon.logic.BeaconState;
import com.mastermarisa.maidbeacon.network.ToggleAuraPayload;
import com.mastermarisa.maidbeacon.utils.EncodeUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class AuraToggleWidget extends AbstractWidget implements TooltipWidget {
    public static final int SIZE = 22;

    private static final int BORDER_ACTIVE = 0xCCFFFFFF;
    private static final int BORDER_IDLE = 0x33FFFFFF;
    private static final int FILL_ACTIVE = 0x2AFFFFFF;
    private static final int FILL_IDLE = 0x22000000;
    private static final int FILL_HOVER = 0x22FFFFFF;

    private final ResourceLocation auraId;
    private final AuraDef aura;
    private final boolean offHand;
    private final Supplier<BeaconState> state;
    private final ResourceLocation icon;
    private final List<Component> tooltip;

    public AuraToggleWidget(ResourceLocation auraId, AuraDef aura, boolean offHand, Supplier<BeaconState> state) {
        super(0, 0, SIZE, SIZE, aura.effect().value().getDisplayName());
        this.auraId = auraId;
        this.aura = aura;
        this.offHand = offHand;
        this.state = state;
        this.icon = effectTexture(aura);
        this.tooltip = List.of(
                aura.effect().value().getDisplayName().copy()
                        .append(Component.literal(" " + EncodeUtils.toRoman(aura.amplifier() + 1))),
                Component.translatable("gui.maidbeacon.tooltip.cost", aura.cost()),
                Component.translatable("gui.maidbeacon.tooltip.range", aura.range()));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean active = state.get().has(auraId);
        int x = getX();
        int y = getY();

        graphics.fill(x, y, x + width, y + height, active ? BORDER_ACTIVE : BORDER_IDLE);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, active ? FILL_ACTIVE : FILL_IDLE);
        if (isHovered) {
            graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, FILL_HOVER);
        }
        graphics.blit(icon, x + 2, y + 2, 0, 0.0F, 0.0F, 18, 18, 18, 18);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        PacketDistributor.sendToServer(new ToggleAuraPayload(offHand, auraId));
    }

    @Override
    public @NotNull List<Component> tooltip() {
        return tooltip;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, getMessage());
    }

    private static ResourceLocation effectTexture(AuraDef aura) {
        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(aura.effect().value());
        return ResourceLocation.fromNamespaceAndPath(effectId.getNamespace(),
                "textures/mob_effect/" + effectId.getPath() + ".png");
    }
}

