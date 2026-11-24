package com.mastermarisa.maidbeacon.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class SettingToggleWidget extends AbstractWidget {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 20;

    private static final int BORDER = 0x66FFFFFF;
    private static final int FILL_ON = 0x332BA84A;
    private static final int FILL_OFF = 0x33000000;
    private static final int FILL_HOVER = 0x22FFFFFF;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private final Component label;
    private final java.util.function.BooleanSupplier getter;
    private final Runnable toggle;

    public SettingToggleWidget(Component label, java.util.function.BooleanSupplier getter, Runnable toggle) {
        super(0, 0, WIDTH, HEIGHT, label);
        this.label = label;
        this.getter = getter;
        this.toggle = toggle;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        boolean on = getter.getAsBoolean();

        graphics.fill(x, y, x + width, y + height, BORDER);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, on ? FILL_ON : FILL_OFF);
        if (isHovered) {
            graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, FILL_HOVER);
        }

        Component value = Component.translatable(on ? "gui.maidbeacon.state.on" : "gui.maidbeacon.state.off");
        int textY = y + (height - 8) / 2;
        graphics.drawCenteredString(net.minecraft.client.Minecraft.getInstance().font, value,
                x + width / 2, textY, TEXT_COLOR);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        toggle.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, label);
    }

    public @NotNull Component label() {
        return label;
    }
}

