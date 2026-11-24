package com.mastermarisa.maidbeacon.client.screen.widget;

import com.mastermarisa.maidbeacon.api.TierDef;
import com.mastermarisa.maidbeacon.logic.BeaconState;
import com.mastermarisa.maidbeacon.network.UpgradePayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class UpgradeWidget extends AbstractWidget implements TooltipWidget {
    public static final int SIZE = 22;

    private static final int BORDER_READY = 0xCCFFFFFF;
    private static final int BORDER_IDLE = 0x33FFFFFF;
    private static final int FILL_READY = 0x2AFFFFFF;
    private static final int FILL_IDLE = 0x22000000;
    private static final int FILL_HOVER = 0x22FFFFFF;

    private final TierDef tier;
    private final boolean offHand;
    private final Supplier<BeaconState> state;
    private final ItemStack iconStack;
    private final Component label;

    public UpgradeWidget(TierDef tier, boolean offHand, Supplier<BeaconState> state) {
        super(0, 0, SIZE, SIZE, Component.translatable("gui.maidbeacon.upgrade.to", tier.level() + 1));
        this.tier = tier;
        this.offHand = offHand;
        this.state = state;
        this.iconStack = firstMaterial(tier);
        this.label = Component.translatable("gui.maidbeacon.upgrade.to", tier.level() + 1);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        boolean ready = ready();
        int x = getX();
        int y = getY();

        graphics.fill(x, y, x + width, y + height, ready ? BORDER_READY : BORDER_IDLE);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, ready ? FILL_READY : FILL_IDLE);
        if (isHovered) {
            graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, FILL_HOVER);
        }
        if (!iconStack.isEmpty()) {
            graphics.renderItem(iconStack, x + 3, y + 3);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (ready()) {
            PacketDistributor.sendToServer(new UpgradePayload(offHand));
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, label);
    }

    @Override
    public @NotNull List<Component> tooltip() {
        boolean ready = ready();
        Component head = label.copy().withStyle(ready ? ChatFormatting.AQUA : ChatFormatting.GRAY);
        TierDef.Upgrade upgrade = tier.upgrade().orElse(null);
        if (upgrade == null) {
            return List.of(head);
        }
        String material = iconStack.isEmpty() ? "?" : iconStack.getHoverName().getString();
        return List.of(head, Component.translatable("gui.maidbeacon.upgrade.material",
                material + " x" + upgrade.count()).withStyle(ChatFormatting.GRAY));
    }

    private boolean ready() {
        return state.get().level() == tier.level();
    }

    private static ItemStack firstMaterial(TierDef tier) {
        TierDef.Upgrade upgrade = tier.upgrade().orElse(null);
        if (upgrade == null || upgrade.material().getItems().length == 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(upgrade.material().getItems()[0].getItem());
    }
}

