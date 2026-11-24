package com.mastermarisa.maidbeacon.client.screen;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mastermarisa.maidbeacon.client.screen.widget.SettingToggleWidget;
import com.mastermarisa.maidbeacon.content.RenderSettingsAttachment;
import com.mastermarisa.maidbeacon.data.ExtraRenderSettings;
import com.mastermarisa.maidbeacon.network.SyncRenderSettingsPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class RenderSettingsScreen extends Screen {
    private static final int ROW_HEIGHT = 26;
    private static final int LABEL_WIDTH = 170;

    private final EntityMaid maid;
    private final ExtraRenderSettings settings;
    private final List<SettingToggleWidget> toggles = new ArrayList<>();

    public RenderSettingsScreen(EntityMaid maid) {
        super(Component.translatable("gui.maidbeacon.render_settings.title"));
        this.maid = maid;
        this.settings = maid.getData(RenderSettingsAttachment.TYPE.get());
    }

    public static void open(EntityMaid maid) {
        Minecraft.getInstance().setScreen(new RenderSettingsScreen(maid));
    }

    @Override
    protected void init() {
        toggles.clear();
        clearWidgets();

        addToggle("enable_rendering",
                s -> s.enableRendering, (s, v) -> s.enableRendering = v);
        addToggle("enable_beacon_beam",
                s -> s.enableBeaconBeam, (s, v) -> s.enableBeaconBeam = v);
        addToggle("enable_beacon_model",
                s -> s.enableBeaconModel, (s, v) -> s.enableBeaconModel = v);
        addToggle("enable_head_halo",
                s -> s.enableHeadHalo, (s, v) -> s.enableHeadHalo = v);
        addToggle("enable_sky_halo",
                s -> s.enableSkyHalo, (s, v) -> s.enableSkyHalo = v);

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(width / 2 - 60, height - 32, 120, 20)
                .build());

        layout();
    }

    @Override
    public void tick() {
        if (!maid.isAlive()) {
            onClose();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
        for (SettingToggleWidget toggle : toggles) {
            graphics.drawString(font, toggle.label(), toggle.getX() - LABEL_WIDTH, toggle.getY() + 6, 0xFFFFFF);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void addToggle(String key,
                           Function<ExtraRenderSettings, Boolean> getter,
                           BiConsumer<ExtraRenderSettings, Boolean> setter) {
        Component label = Component.translatable("gui.maidbeacon.render_setting." + key);
        SettingToggleWidget widget = new SettingToggleWidget(
                label,
                () -> getter.apply(settings),
                () -> {
                    setter.accept(settings, !getter.apply(settings));
                    pushUpdate();
                });
        toggles.add(widget);
        addRenderableWidget(widget);
    }

    private void layout() {
        int totalHeight = toggles.size() * ROW_HEIGHT;
        int y = (height - totalHeight) / 2;
        for (SettingToggleWidget toggle : toggles) {
            toggle.setPosition(width / 2, y);
            y += ROW_HEIGHT;
        }
    }

    private void pushUpdate() {
        PacketDistributor.sendToServer(new SyncRenderSettingsPayload(maid.getId(), settings));
    }
}

