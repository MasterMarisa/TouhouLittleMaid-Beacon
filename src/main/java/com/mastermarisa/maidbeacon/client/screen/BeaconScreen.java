package com.mastermarisa.maidbeacon.client.screen;

import com.mastermarisa.maidbeacon.client.screen.widget.AuraToggleWidget;
import com.mastermarisa.maidbeacon.client.screen.widget.TooltipWidget;
import com.mastermarisa.maidbeacon.client.screen.widget.UpgradeWidget;
import com.mastermarisa.maidbeacon.init.ModItems;
import com.mastermarisa.maidbeacon.logic.BeaconIndex;
import com.mastermarisa.maidbeacon.logic.BeaconRules;
import com.mastermarisa.maidbeacon.logic.BeaconService;
import com.mastermarisa.maidbeacon.logic.BeaconState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BeaconScreen extends Screen {
    private static final int TOP = 48;
    private static final int BOTTOM = 24;
    private static final int GAP = 16;
    private static final int ROW_GAP = 12;
    private static final double SCROLL_STEP = 14.0D;

    private final InteractionHand hand;
    private final boolean offHand;
    private final List<List<AbstractWidget>> rows = new ArrayList<>();

    private int builtForLevel = Integer.MIN_VALUE;
    private double scroll;

    private BeaconScreen(InteractionHand hand) {
        super(Component.translatable("gui.maidbeacon.beacon.title"));
        this.hand = hand;
        this.offHand = hand == InteractionHand.OFF_HAND;
    }

    public static void open(InteractionHand hand) {
        Minecraft.getInstance().setScreen(new BeaconScreen(hand));
    }

    @Override
    protected void init() {
        rebuild();
    }

    @Override
    public void tick() {
        Player player = Minecraft.getInstance().player;
        if (player == null || !player.getItemInHand(hand).is(ModItems.MOBILE_BEACON.get())) {
            onClose();
            return;
        }
        if (state().level() != builtForLevel) {
            rebuild();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        BeaconState state = state();
        graphics.drawCenteredString(font, title, width / 2, TOP - 34, 0xFFFFFF);
        graphics.drawCenteredString(font,
                Component.translatable("tooltip.maidbeacon.level", state.level()), width / 2, TOP - 22, 0xA0A0A0);
        graphics.drawCenteredString(font,
                Component.translatable("gui.maidbeacon.budget",
                        usedCost(state), budget(state)), width / 2, TOP - 10, 0x808080);

        renderHoveredTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY == 0.0D) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        scroll += scrollY * SCROLL_STEP;
        layout();
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void rebuild() {
        rows.clear();
        clearWidgets();

        RegistryAccess access = registryAccess();
        if (access == null) {
            return;
        }

        BeaconState state = state();
        List<BeaconIndex.Entry> auras = BeaconIndex.auraEntries(access);

        for (int level = 0; level <= state.level(); level++) {
            List<AbstractWidget> auraRow = new ArrayList<>();
            for (BeaconIndex.Entry entry : auras) {
                if (entry.def().tier() == level) {
                    auraRow.add(new AuraToggleWidget(entry.id(), entry.def(), offHand, this::state));
                }
            }
            if (!auraRow.isEmpty()) {
                addRow(auraRow);
            }
        }

        BeaconIndex.tier(access, state.level())
                .filter(tier -> !tier.isMaxLevel())
                .ifPresent(tier -> addRow(List.of(new UpgradeWidget(tier, offHand, this::state))));

        builtForLevel = state.level();
        layout();
    }

    private void addRow(List<AbstractWidget> row) {
        rows.add(row);
        row.forEach(this::addRenderableWidget);
    }

    private void layout() {
        int viewHeight = height - TOP - BOTTOM;
        int needed = 0;
        for (List<AbstractWidget> row : rows) {
            needed += rowHeight(row) + ROW_GAP;
        }
        scroll = Math.clamp(scroll, Math.min(0, viewHeight - needed), 0.0D);

        int y = TOP + (int) scroll;
        for (List<AbstractWidget> row : rows) {
            int totalWidth = row.stream().mapToInt(AbstractWidget::getWidth).sum() + GAP * (row.size() - 1);
            int x = width / 2 - totalWidth / 2;
            for (AbstractWidget widget : row) {
                widget.setPosition(x, y);
                x += widget.getWidth() + GAP;
            }
            y += rowHeight(row) + ROW_GAP;
        }
    }

    private void renderHoveredTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        for (List<AbstractWidget> row : rows) {
            for (AbstractWidget widget : row) {
                if (widget.isHovered() && widget instanceof TooltipWidget tooltip) {
                    graphics.renderComponentTooltip(font, tooltip.tooltip(), mouseX, mouseY);
                    return;
                }
            }
        }
    }

    private static int rowHeight(List<AbstractWidget> row) {
        return row.stream().mapToInt(AbstractWidget::getHeight).max().orElse(0);
    }

    private int usedCost(BeaconState state) {
        RegistryAccess access = registryAccess();
        return access == null ? 0 : BeaconRules.usedCost(access, state);
    }

    private int budget(BeaconState state) {
        RegistryAccess access = registryAccess();
        return access == null ? 0 : BeaconRules.budget(access, state);
    }

    private BeaconState state() {
        Player player = Minecraft.getInstance().player;
        return player == null ? BeaconState.EMPTY : BeaconService.stateOf(player.getItemInHand(hand));
    }

    private static @Nullable RegistryAccess registryAccess() {
        ClientLevel level = Minecraft.getInstance().level;
        return level == null ? null : level.registryAccess();
    }
}

