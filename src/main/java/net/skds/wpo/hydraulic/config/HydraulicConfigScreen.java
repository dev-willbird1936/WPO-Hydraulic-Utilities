package net.skds.wpo.hydraulic.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.skds.wpo.hydraulic.HydraulicConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class HydraulicConfigScreen extends Screen {
    private static final int LABEL_X_OFFSET = 180;
    private static final int CONTROL_X_OFFSET = 18;
    private static final int CONTROL_WIDTH = 142;
    private static final int ROW_HEIGHT = 24;
    private static final int LIST_TOP = 94;
    private static final int LIST_BOTTOM_PADDING = 78;

    private final Screen parent;
    private final List<ToggleOption> toggleOptions = new ArrayList<>();
    private final List<IntOption> intOptions = new ArrayList<>();

    private Page page = Page.SYSTEMS;
    private Button systemsTabButton;
    private Button tuningTabButton;
    private Component statusMessage = CommonComponents.EMPTY;
    private int systemsScroll;
    private int tuningScroll;

    public HydraulicConfigScreen(Screen parent) {
        super(Component.literal("WPO: Hydraulic Utilities Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        toggleOptions.clear();
        intOptions.clear();
        clearWidgets();

        int centerX = this.width / 2;
        int top = LIST_TOP;

        systemsTabButton = this.addRenderableWidget(Button.builder(Component.literal("Systems"), button -> switchPage(Page.SYSTEMS))
            .bounds(centerX - 102, 34, 100, 20)
            .build());
        tuningTabButton = this.addRenderableWidget(Button.builder(Component.literal("Tuning"), button -> switchPage(Page.TUNING))
            .bounds(centerX + 2, 34, 100, 20)
            .build());

        int row = 0;
        addToggle(top + row++ * ROW_HEIGHT, Page.SYSTEMS, "Drains", HydraulicConfig.COMMON.drains.get(), HydraulicConfig.COMMON.drains.getDefault(), HydraulicConfig.COMMON.drains::set);
        addToggle(top + row++ * ROW_HEIGHT, Page.SYSTEMS, "Pumps", HydraulicConfig.COMMON.pumps.get(), HydraulicConfig.COMMON.pumps.getDefault(), HydraulicConfig.COMMON.pumps::set);
        addToggle(top + row++ * ROW_HEIGHT, Page.SYSTEMS, "Nozzles", HydraulicConfig.COMMON.nozzles.get(), HydraulicConfig.COMMON.nozzles.getDefault(), HydraulicConfig.COMMON.nozzles::set);
        addToggle(top + row++ * ROW_HEIGHT, Page.SYSTEMS, "Creative Sources", HydraulicConfig.COMMON.creativeSources.get(), HydraulicConfig.COMMON.creativeSources.getDefault(), HydraulicConfig.COMMON.creativeSources::set);
        addToggle(top + row++ * ROW_HEIGHT, Page.SYSTEMS, "Valves", HydraulicConfig.COMMON.valves.get(), HydraulicConfig.COMMON.valves.getDefault(), HydraulicConfig.COMMON.valves::set);
        addToggle(top + row++ * ROW_HEIGHT, Page.SYSTEMS, "Grates", HydraulicConfig.COMMON.grates.get(), HydraulicConfig.COMMON.grates.getDefault(), HydraulicConfig.COMMON.grates::set);
        addToggle(top + row++ * ROW_HEIGHT, Page.SYSTEMS, "Watertight Doors", HydraulicConfig.COMMON.watertightDoors.get(), HydraulicConfig.COMMON.watertightDoors.getDefault(), HydraulicConfig.COMMON.watertightDoors::set);
        addToggle(top + row++ * ROW_HEIGHT, Page.SYSTEMS, "Watertight Trapdoors", HydraulicConfig.COMMON.watertightTrapdoors.get(), HydraulicConfig.COMMON.watertightTrapdoors.getDefault(), HydraulicConfig.COMMON.watertightTrapdoors::set);
        addToggle(top + row++ * ROW_HEIGHT, Page.SYSTEMS, "Redstone Control", HydraulicConfig.COMMON.redstoneControl.get(), HydraulicConfig.COMMON.redstoneControl.getDefault(), HydraulicConfig.COMMON.redstoneControl::set);
        addToggle(top + row * ROW_HEIGHT, Page.SYSTEMS, "Void Excess Water", HydraulicConfig.COMMON.voidExcessWater.get(), HydraulicConfig.COMMON.voidExcessWater.getDefault(), HydraulicConfig.COMMON.voidExcessWater::set);

        row = 0;
        addIntField(top + row++ * ROW_HEIGHT, Page.TUNING, "Machine Tank Buckets", HydraulicConfig.COMMON.machineTankBuckets.get(), HydraulicConfig.COMMON.machineTankBuckets.getDefault(), 1, 256, HydraulicConfig.COMMON.machineTankBuckets::set);
        addIntField(top + row++ * ROW_HEIGHT, Page.TUNING, "Drain Throughput", HydraulicConfig.COMMON.drainThroughputLevels.get(), HydraulicConfig.COMMON.drainThroughputLevels.getDefault(), 1, 8, HydraulicConfig.COMMON.drainThroughputLevels::set);
        addIntField(top + row++ * ROW_HEIGHT, Page.TUNING, "Pump Throughput", HydraulicConfig.COMMON.pumpThroughputLevels.get(), HydraulicConfig.COMMON.pumpThroughputLevels.getDefault(), 1, 8, HydraulicConfig.COMMON.pumpThroughputLevels::set);
        addIntField(top + row++ * ROW_HEIGHT, Page.TUNING, "Nozzle Throughput", HydraulicConfig.COMMON.nozzleThroughputLevels.get(), HydraulicConfig.COMMON.nozzleThroughputLevels.getDefault(), 1, 8, HydraulicConfig.COMMON.nozzleThroughputLevels::set);
        addIntField(top + row * ROW_HEIGHT, Page.TUNING, "Creative Source Output", HydraulicConfig.COMMON.creativeSourceThroughputLevels.get(), HydraulicConfig.COMMON.creativeSourceThroughputLevels.getDefault(), 1, 8, HydraulicConfig.COMMON.creativeSourceThroughputLevels::set);

        this.addRenderableWidget(Button.builder(Component.literal("Defaults"), button -> loadDefaults())
            .bounds(centerX - 154, this.height - 28, 100, 20)
            .build());
        this.addRenderableWidget(Button.builder(Component.literal("Save"), button -> saveAndClose())
            .bounds(centerX - 50, this.height - 28, 100, 20)
            .build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
            .bounds(centerX + 54, this.height - 28, 100, 20)
            .build());

        updatePageState();
    }

    private void addToggle(int y, Page page, String label, boolean initialValue, boolean defaultValue, Consumer<Boolean> setter) {
        int centerX = this.width / 2;
        CycleButton<Boolean> button = this.addRenderableWidget(CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
            .withInitialValue(initialValue)
            .displayOnlyValue()
            .create(centerX + CONTROL_X_OFFSET, y, CONTROL_WIDTH, 20, CommonComponents.EMPTY));
        toggleOptions.add(new ToggleOption(Component.literal(label), button, setter, defaultValue, y, page));
    }

    private void addIntField(int y, Page page, String label, int initialValue, int defaultValue, int min, int max, IntConsumer setter) {
        int centerX = this.width / 2;
        EditBox box = new EditBox(this.font, centerX + CONTROL_X_OFFSET, y, CONTROL_WIDTH, 20, Component.literal(label));
        box.setValue(String.valueOf(initialValue));
        this.addRenderableWidget(box);
        intOptions.add(new IntOption(Component.literal(label), box, setter, min, max, defaultValue, y, page));
    }

    private void switchPage(Page page) {
        this.page = page;
        updatePageState();
    }

    private void updatePageState() {
        clampScroll();
        systemsTabButton.active = page != Page.SYSTEMS;
        tuningTabButton.active = page != Page.TUNING;
        int scroll = getScroll(page);
        int viewportTop = LIST_TOP;
        int viewportBottom = this.height - LIST_BOTTOM_PADDING;
        toggleOptions.forEach(option -> option.updateLayout(page, scroll, viewportTop, viewportBottom));
        intOptions.forEach(option -> option.updateLayout(page, scroll, viewportTop, viewportBottom));
    }

    private void loadDefaults() {
        toggleOptions.forEach(option -> option.button().setValue(option.defaultValue()));
        intOptions.forEach(option -> option.box().setValue(String.valueOf(option.defaultValue())));
        statusMessage = Component.literal("Defaults loaded. Press Save to apply.");
    }

    private void clampScroll() {
        systemsScroll = Mth.clamp(systemsScroll, 0, getMaxScroll(Page.SYSTEMS));
        tuningScroll = Mth.clamp(tuningScroll, 0, getMaxScroll(Page.TUNING));
    }

    private int getScroll(Page page) {
        return page == Page.SYSTEMS ? systemsScroll : tuningScroll;
    }

    private void setScroll(Page page, int value) {
        if (page == Page.SYSTEMS) {
            systemsScroll = value;
        } else {
            tuningScroll = value;
        }
    }

    private int getMaxScroll(Page page) {
        int contentBottom = 0;
        for (ToggleOption option : toggleOptions) {
            if (option.page() == page) {
                contentBottom = Math.max(contentBottom, option.baseY() + 20);
            }
        }
        for (IntOption option : intOptions) {
            if (option.page() == page) {
                contentBottom = Math.max(contentBottom, option.baseY() + 20);
            }
        }
        int viewportHeight = this.height - LIST_BOTTOM_PADDING - LIST_TOP;
        return Math.max(0, contentBottom - LIST_TOP - viewportHeight);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int maxScroll = getMaxScroll(page);
        if (maxScroll <= 0) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        int step = ROW_HEIGHT;
        int next = Mth.clamp(getScroll(page) - (int) Math.signum(scrollY) * step, 0, maxScroll);
        if (next != getScroll(page)) {
            setScroll(page, next);
            updatePageState();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void saveAndClose() {
        try {
            for (ToggleOption option : toggleOptions) {
                option.setter.accept(option.button.getValue());
            }
            for (IntOption option : intOptions) {
                option.setter.accept(parseInt(option.box.getValue(), option.min, option.max));
            }
            HydraulicConfig.save();
            this.minecraft.setScreen(parent);
        } catch (NumberFormatException ex) {
            statusMessage = Component.literal("Enter whole numbers for every field.");
        }
    }

    private static int parseInt(String text, int min, int max) {
        return Mth.clamp(Integer.parseInt(text.trim()), min, max);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int labelX = centerX - LABEL_X_OFFSET;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, 12, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, page.title, centerX, 66, 0xE0E0E0);

        for (ToggleOption option : toggleOptions) {
            if (option.isVisible()) {
                guiGraphics.drawString(this.font, option.label(), labelX, option.button().getY() + (20 - this.font.lineHeight) / 2, 0xFFFFFF, false);
            }
        }
        for (IntOption option : intOptions) {
            if (option.isVisible()) {
                guiGraphics.drawString(this.font, option.label(), labelX, option.box().getY() + (20 - this.font.lineHeight) / 2, 0xFFFFFF, false);
            }
        }

        guiGraphics.drawCenteredString(this.font,
            Component.literal("For extended tuning beyond this screen, edit the TOML directly."),
            centerX, this.height - 52, 0xC0C0C0);

        int maxScroll = getMaxScroll(page);
        if (maxScroll > 0) {
            guiGraphics.drawCenteredString(this.font,
                Component.literal("Mouse wheel to scroll"),
                centerX, this.height - 64, 0xA0A0A0);
        }

        if (!statusMessage.getString().isEmpty()) {
            guiGraphics.drawCenteredString(this.font, statusMessage, centerX, this.height - 40, 0xFF8080);
        }
    }

    private enum Page {
        SYSTEMS(Component.literal("Systems")),
        TUNING(Component.literal("Tuning"));

        private final Component title;

        Page(Component title) {
            this.title = title;
        }
    }

    private interface PagedOption {
        void updateLayout(Page page, int scroll, int viewportTop, int viewportBottom);
    }

    private record ToggleOption(Component label, CycleButton<Boolean> button, Consumer<Boolean> setter, boolean defaultValue, int baseY, Page page) implements PagedOption {
        @Override
        public void updateLayout(Page currentPage, int scroll, int viewportTop, int viewportBottom) {
            int y = baseY - scroll;
            button.setY(y);
            boolean visible = this.page == currentPage && y + 20 > viewportTop && y < viewportBottom;
            button.visible = visible;
            button.active = visible;
        }

        public boolean isVisible() {
            return button.visible;
        }
    }

    private record IntOption(Component label, EditBox box, IntConsumer setter, int min, int max, int defaultValue, int baseY, Page page) implements PagedOption {
        @Override
        public void updateLayout(Page currentPage, int scroll, int viewportTop, int viewportBottom) {
            int y = baseY - scroll;
            box.setY(y);
            boolean visible = this.page == currentPage && y + 20 > viewportTop && y < viewportBottom;
            box.visible = visible;
            box.setEditable(visible);
            box.active = visible;
        }

        public boolean isVisible() {
            return box.visible;
        }
    }
}
