package de.keksuccino.fancymenu.customization.layout.editor.actions;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.keksuccino.fancymenu.customization.action.ActionInstance;
import de.keksuccino.fancymenu.customization.action.Executable;
import de.keksuccino.fancymenu.customization.action.blocks.AbstractExecutableBlock;
import de.keksuccino.fancymenu.customization.action.blocks.GenericExecutableBlock;
import de.keksuccino.fancymenu.customization.action.blocks.statements.ElseExecutableBlock;
import de.keksuccino.fancymenu.customization.action.blocks.statements.ElseIfExecutableBlock;
import de.keksuccino.fancymenu.customization.action.blocks.statements.IfExecutableBlock;
import de.keksuccino.fancymenu.customization.loadingrequirement.internal.LoadingRequirementGroup;
import de.keksuccino.fancymenu.customization.loadingrequirement.internal.LoadingRequirementInstance;
import de.keksuccino.fancymenu.util.rendering.ui.screen.ConfirmationScreen;
import de.keksuccino.fancymenu.util.rendering.ui.UIBase;
import de.keksuccino.fancymenu.util.rendering.ui.scroll.v1.scrollarea.ScrollArea;
import de.keksuccino.fancymenu.util.rendering.ui.scroll.v1.scrollarea.entry.ScrollAreaEntry;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import de.keksuccino.fancymenu.util.rendering.ui.widget.ExtendedButton;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.konkrete.input.MouseInput;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public class ManageActionsScreen extends Screen {

    protected GenericExecutableBlock executableBlock;
    protected Consumer<GenericExecutableBlock> callback;

    protected ScrollArea actionsScrollArea = new ScrollArea(0, 0, 0, 0);
    protected ExtendedButton addActionButton;
    protected ExtendedButton moveUpButton;
    protected ExtendedButton moveDownButton;
    protected ExtendedButton editButton;
    protected ExtendedButton removeButton;
    protected ExtendedButton doneButton;
    protected ExtendedButton cancelButton;
    protected ExtendedButton addIfButton;
    protected ExtendedButton appendElseIfButton;
    protected ExtendedButton appendElseButton;

    @Nullable
    protected ExecutableEntry renderTickDragHoveredEntry = null;
    @Nullable
    protected ExecutableEntry renderTickDraggedEntry = null;

    private final ExecutableEntry BEFORE_FIRST = new ExecutableEntry(this.actionsScrollArea, new GenericExecutableBlock(), 1, 0);
    private final ExecutableEntry AFTER_LAST = new ExecutableEntry(this.actionsScrollArea, new GenericExecutableBlock(), 1, 0);

    //TODO add logic to the 3 new buttons
    //TODO add logic to the 3 new buttons
    //TODO add logic to the 3 new buttons
    //TODO add logic to the 3 new buttons
    //TODO add logic to the 3 new buttons

    public ManageActionsScreen(@NotNull GenericExecutableBlock executableBlock, @NotNull Consumer<GenericExecutableBlock> callback) {

        super(Component.translatable("fancymenu.editor.action.screens.manage_screen.manage"));

        this.executableBlock = executableBlock.copy(false);
        this.callback = callback;
        this.updateActionInstanceScrollArea(false);

    }

    @Override
    protected void init() {

        this.addActionButton = new ExtendedButton(0, 0, 150, 20, I18n.get("fancymenu.editor.action.screens.add_action"), (button) -> {
            BuildActionScreen s = new BuildActionScreen(null, (call) -> {
                if (call != null) {
                    this.executableBlock.addExecutable(call);
                    this.updateActionInstanceScrollArea(false);
                }
                Minecraft.getInstance().setScreen(this);
            });
            Minecraft.getInstance().setScreen(s);
        });
        this.addWidget(this.addActionButton);
        this.addActionButton.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.add_action.desc")));
        UIBase.applyDefaultWidgetSkinTo(this.addActionButton);

        this.moveUpButton = new ExtendedButton(0, 0, 150, 20, I18n.get("fancymenu.editor.action.screens.move_action_up"), (button) -> {
            this.moveUp(this.getSelectedEntry());
        }) {
            @Override
            public void render(@NotNull PoseStack p_93657_, int p_93658_, int p_93659_, float p_93660_) {
                ManageActionsScreen s = ManageActionsScreen.this;
                if (!s.isAnyExecutableSelected()) {
                    this.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.finish.no_action_selected")));
                    this.active = false;
                } else {
                    this.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.move_action_up.desc")));
                    this.active = true;
                }
                super.render(p_93657_, p_93658_, p_93659_, p_93660_);
            }
        };
        this.addWidget(this.moveUpButton);
        UIBase.applyDefaultWidgetSkinTo(this.moveUpButton);

        this.moveDownButton = new ExtendedButton(0, 0, 150, 20, I18n.get("fancymenu.editor.action.screens.move_action_down"), (button) -> {
            this.moveDown(this.getSelectedEntry());
        }) {
            @Override
            public void render(@NotNull PoseStack p_93657_, int p_93658_, int p_93659_, float p_93660_) {
                ManageActionsScreen s = ManageActionsScreen.this;
                if (!s.isAnyExecutableSelected()) {
                    this.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.finish.no_action_selected")));
                    this.active = false;
                } else {
                    this.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.move_action_down.desc")));
                    this.active = true;
                }
                super.render(p_93657_, p_93658_, p_93659_, p_93660_);
            }
        };
        this.addWidget(this.moveDownButton);
        UIBase.applyDefaultWidgetSkinTo(this.moveDownButton);

        this.editButton = new ExtendedButton(0, 0, 150, 20, I18n.get("fancymenu.editor.action.screens.edit_action"), (button) -> {
            ExecutableEntry selected = this.getSelectedEntry();
            if (selected != null) {
                AbstractExecutableBlock block = selected.getParentBlock();
                if (selected.executable instanceof ActionInstance i) {
                    BuildActionScreen s = new BuildActionScreen(i.copy(false), (call) -> {
                        if (call != null) {
                            int index = block.getExecutables().indexOf(selected.executable);
                            block.getExecutables().remove(selected.executable);
                            if (index != -1) {
                                block.getExecutables().add(index, call);
                            } else {
                                block.getExecutables().add(call);
                            }
                            this.updateActionInstanceScrollArea(false);
                        }
                        Minecraft.getInstance().setScreen(this);
                    });
                    Minecraft.getInstance().setScreen(s);
                }
            }
        }) {
            @Override
            public void render(@NotNull PoseStack p_93657_, int p_93658_, int p_93659_, float p_93660_) {
                ManageActionsScreen s = ManageActionsScreen.this;
                if (!s.isAnyExecutableSelected()) {
                    this.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.finish.no_action_selected")));
                    this.active = false;
                } else {
                    this.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.edit_action.desc")));
                    this.active = true;
                }
                super.render(p_93657_, p_93658_, p_93659_, p_93660_);
            }
        };
        this.addWidget(this.editButton);
        UIBase.applyDefaultWidgetSkinTo(this.editButton);

        this.removeButton = new ExtendedButton(0, 0, 150, 20, I18n.get("fancymenu.editor.action.screens.remove_action"), (button) -> {
            ExecutableEntry selected = this.getSelectedEntry();
            if (selected != null) {
                Minecraft.getInstance().setScreen(ConfirmationScreen.ofStrings((call) -> {
                    if (call) {
                        selected.getParentBlock().getExecutables().remove(selected.executable);
                        this.updateActionInstanceScrollArea(true);
                    }
                    Minecraft.getInstance().setScreen(this);
                }, LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.remove_action.confirm")));
            }
        }) {
            @Override
            public void render(@NotNull PoseStack p_93657_, int p_93658_, int p_93659_, float p_93660_) {
                ManageActionsScreen s = ManageActionsScreen.this;
                if (!s.isAnyExecutableSelected()) {
                    this.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.finish.no_action_selected")));
                    this.active = false;
                } else {
                    this.setTooltip(Tooltip.of(LocalizationUtils.splitLocalizedStringLines("fancymenu.editor.action.screens.remove_action.desc")));
                    this.active = true;
                }
                super.render(p_93657_, p_93658_, p_93659_, p_93660_);
            }
        };
        this.addWidget(this.removeButton);
        UIBase.applyDefaultWidgetSkinTo(this.removeButton);

        this.doneButton = new ExtendedButton(0, 0, 150, 20, I18n.get("fancymenu.guicomponents.done"), (button) -> {
            this.callback.accept(this.executableBlock);
        });
        this.addWidget(this.doneButton);
        UIBase.applyDefaultWidgetSkinTo(this.doneButton);

        this.cancelButton = new ExtendedButton(0, 0, 150, 20, Component.translatable("fancymenu.guicomponents.cancel"), (button) -> {
            this.callback.accept(null);
        });
        this.addWidget(this.cancelButton);
        UIBase.applyDefaultWidgetSkinTo(this.cancelButton);

    }

    @Override
    public void onClose() {
        this.callback.accept(null);
    }

    @Override
    public void render(@NotNull PoseStack pose, int mouseX, int mouseY, float partial) {

        this.renderTickDragHoveredEntry = this.getDragHoveredEntry();
        this.renderTickDraggedEntry = this.getDraggedEntry();

        //Auto-scroll scroll area vertically if dragged and out-of-area
        if (this.renderTickDraggedEntry != null) {
            float scrollOffset = 0.1F * this.actionsScrollArea.verticalScrollBar.getWheelScrollSpeed();
            if (MouseInput.getMouseY() <= this.actionsScrollArea.getInnerY()) {
                this.actionsScrollArea.verticalScrollBar.setScroll(this.actionsScrollArea.verticalScrollBar.getScroll() - scrollOffset);
            }
            if (MouseInput.getMouseY() >= (this.actionsScrollArea.getInnerY() + this.actionsScrollArea.getInnerHeight())) {
                this.actionsScrollArea.verticalScrollBar.setScroll(this.actionsScrollArea.verticalScrollBar.getScroll() + scrollOffset);
            }
        }

        fill(pose, 0, 0, this.width, this.height, UIBase.getUIColorTheme().screen_background_color.getColorInt());

        Component titleComp = this.title.copy().withStyle(Style.EMPTY.withBold(true));
        this.font.draw(pose, titleComp, 20, 20, UIBase.getUIColorTheme().generic_text_base_color.getColorInt());

        this.font.draw(pose, I18n.get("fancymenu.editor.action.screens.manage_screen.actions"), 20, 50, UIBase.getUIColorTheme().generic_text_base_color.getColorInt());

        this.actionsScrollArea.setWidth(this.width - 20 - 150 - 20 - 20, true);
        this.actionsScrollArea.setHeight(this.height - 85, true);
        this.actionsScrollArea.setX(20, true);
        this.actionsScrollArea.setY(50 + 15, true);
        this.actionsScrollArea.render(pose, mouseX, mouseY, partial);

        //Render line to visualize where the dragged entry gets dropped when stop dragging it
        if (this.renderTickDragHoveredEntry != null) {
            int dY = this.renderTickDragHoveredEntry.getY();
            int dH = this.renderTickDragHoveredEntry.getHeight();
            if (this.renderTickDragHoveredEntry == BEFORE_FIRST) {
                dY = this.actionsScrollArea.getInnerY();
                dH = 1;
            }
            if (this.renderTickDragHoveredEntry == AFTER_LAST) {
                dY = this.actionsScrollArea.getInnerY() + this.actionsScrollArea.getInnerHeight() - 1;
                dH = 1;
            }
            fill(pose, this.actionsScrollArea.getInnerX(), dY + dH - 1, this.actionsScrollArea.getInnerX() + this.actionsScrollArea.getInnerWidth(), dY + dH, UIBase.getUIColorTheme().description_area_text_color.getColorInt());
        }

        this.doneButton.setX(this.width - 20 - this.doneButton.getWidth());
        this.doneButton.setY(this.height - 20 - 20);
        this.doneButton.render(pose, mouseX, mouseY, partial);

        this.cancelButton.setX(this.width - 20 - this.cancelButton.getWidth());
        this.cancelButton.setY(this.doneButton.getY() - 5 - 20);
        this.cancelButton.render(pose, mouseX, mouseY, partial);

        this.removeButton.setX(this.width - 20 - this.removeButton.getWidth());
        this.removeButton.setY(this.cancelButton.getY() - 15 - 20);
        this.removeButton.render(pose, mouseX, mouseY, partial);

        this.editButton.setX(this.width - 20 - this.editButton.getWidth());
        this.editButton.setY(this.removeButton.getY() - 5 - 20);
        this.editButton.render(pose, mouseX, mouseY, partial);

        this.moveDownButton.setX(this.width - 20 - this.moveDownButton.getWidth());
        this.moveDownButton.setY(this.editButton.getY() - 5 - 20);
        this.moveDownButton.render(pose, mouseX, mouseY, partial);

        this.moveUpButton.setX(this.width - 20 - this.moveUpButton.getWidth());
        this.moveUpButton.setY(this.moveDownButton.getY() - 5 - 20);
        this.moveUpButton.render(pose, mouseX, mouseY, partial);

        this.addActionButton.setX(this.width - 20 - this.addActionButton.getWidth());
        this.addActionButton.setY(this.moveUpButton.getY() - 5 - 20);
        this.addActionButton.render(pose, mouseX, mouseY, partial);

        super.render(pose, mouseX, mouseY, partial);

    }

    @Nullable
    protected ExecutableEntry getDragHoveredEntry() {
        ExecutableEntry draggedEntry = this.getDraggedEntry();
        if (draggedEntry != null) {
            if ((MouseInput.getMouseY() <= this.actionsScrollArea.getInnerY()) && (this.actionsScrollArea.verticalScrollBar.getScroll() == 0.0F)) {
                return BEFORE_FIRST;
            }
            if ((MouseInput.getMouseY() >= (this.actionsScrollArea.getInnerY() + this.actionsScrollArea.getInnerHeight())) && (this.actionsScrollArea.verticalScrollBar.getScroll() == 1.0F)) {
                return AFTER_LAST;
            }
            for (ScrollAreaEntry e : this.actionsScrollArea.getEntries()) {
                if (e instanceof ExecutableEntry ee) {
                    if ((e.getY() + e.getHeight()) > (this.actionsScrollArea.getInnerY() + this.actionsScrollArea.getInnerHeight())) {
                        continue;
                    }
                    if ((ee != draggedEntry) && UIBase.isXYInArea(MouseInput.getMouseX(), MouseInput.getMouseY(), ee.getX(), ee.getY(), ee.getWidth(), ee.getHeight()) && this.actionsScrollArea.isMouseInsideArea()) {
                        return ee;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    protected ExecutableEntry getDraggedEntry() {
        for (ScrollAreaEntry e : this.actionsScrollArea.getEntries()) {
            if (e instanceof ExecutableEntry ee) {
                if (ee.dragging) return ee;
            }
        }
        return null;
    }

    @Nullable
    protected ExecutableEntry findEntryForExecutable(Executable executable) {
        ScrollAreaEntry e = this.actionsScrollArea.getFocusedEntry();
        if (e instanceof ExecutableEntry ee) {
            if (ee.executable == executable) return ee;
        }
        return null;
    }

    @Nullable
    protected ExecutableEntry getSelectedEntry() {
        ScrollAreaEntry e = this.actionsScrollArea.getFocusedEntry();
        if (e instanceof ExecutableEntry ee) {
            return ee;
        }
        return null;
    }

    protected boolean isAnyExecutableSelected() {
        return this.getSelectedEntry() != null;
    }

    protected void moveUp(ExecutableEntry entry) {
        if (entry != null) {
            if ((entry.executable instanceof ActionInstance) || (entry.executable instanceof IfExecutableBlock)) {
                AbstractExecutableBlock parentBlock = entry.getParentBlock();
                int index = parentBlock.getExecutables().indexOf(entry.executable);
                if (index > 0) {
                    parentBlock.getExecutables().remove(entry.executable);
                    parentBlock.getExecutables().add(index-1, entry.executable);
                    this.updateActionInstanceScrollArea(true);
                }
            }
            if (entry.executable instanceof ElseIfExecutableBlock ei) {
                AbstractExecutableBlock entryAppendParent = entry.appendParent;
                if (entryAppendParent != null) {
                    ExecutableEntry appendParentEntry = this.findEntryForExecutable(entry.appendParent);
                    if (appendParentEntry != null) {
                        AbstractExecutableBlock appendGrandParent = appendParentEntry.appendParent;
                        if (appendGrandParent != null) {
                            ei.setAppendedBlock(entryAppendParent);
                            appendGrandParent.setAppendedBlock(ei);
                        }
                    }
                }
            }
            //Re-select entry after updating scroll area
            ExecutableEntry newEntry = this.findEntryForExecutable(entry.executable);
            if (newEntry != null) {
                newEntry.setSelected(true);
            }
        }
    }

    protected void moveDown(ExecutableEntry entry) {
        if (entry != null) {
            if ((entry.executable instanceof ActionInstance) || (entry.executable instanceof IfExecutableBlock)) {
                AbstractExecutableBlock parentBlock = entry.getParentBlock();
                int index = parentBlock.getExecutables().indexOf(entry.executable);
                if ((index >= 0) && (index <= parentBlock.getExecutables().size()-2)) {
                    parentBlock.getExecutables().remove(entry.executable);
                    parentBlock.getExecutables().add(index+1, entry.executable);
                    this.updateActionInstanceScrollArea(true);
                }
            }
            if (entry.executable instanceof ElseIfExecutableBlock ei) {
                AbstractExecutableBlock entryAppendChild = ((AbstractExecutableBlock)entry.executable).getAppendedBlock();
                AbstractExecutableBlock entryAppendParent = entry.appendParent;
                if ((entryAppendChild instanceof ElseIfExecutableBlock) && (entryAppendParent != null)) {
                    entryAppendChild.setAppendedBlock(ei);
                    entryAppendParent.setAppendedBlock(entryAppendChild);
                }
            }
            //Re-select entry after updating scroll area
            ExecutableEntry newEntry = this.findEntryForExecutable(entry.executable);
            if (newEntry != null) {
                newEntry.setSelected(true);
            }
        }
    }

//    protected void updateActionInstanceScrollArea(boolean keepScroll) {
//
//        float oldScrollVertical = this.actionsScrollArea.verticalScrollBar.getScroll();
//        float oldScrollHorizontal = this.actionsScrollArea.horizontalScrollBar.getScroll();
//
//        this.actionsScrollArea.clearEntries();
//
//        for (Executable i : this.executableBlock.getExecutables()) {
//            ExecutableEntry e = new ExecutableEntry(this.actionsScrollArea, i, 14, 0);
//            this.actionsScrollArea.addEntry(e);
//        }
//
//        if (keepScroll) {
//            this.actionsScrollArea.verticalScrollBar.setScroll(oldScrollVertical);
//            this.actionsScrollArea.horizontalScrollBar.setScroll(oldScrollHorizontal);
//        }
//
//    }

    protected void updateActionInstanceScrollArea(boolean keepScroll) {

        for (ScrollAreaEntry e : this.actionsScrollArea.getEntries()) {
            if (e instanceof ExecutableEntry ee) {
                ee.leftMouseDownDragging = false;
                ee.dragging = false;
            }
        }

        float oldScrollVertical = this.actionsScrollArea.verticalScrollBar.getScroll();
        float oldScrollHorizontal = this.actionsScrollArea.horizontalScrollBar.getScroll();

        this.actionsScrollArea.clearEntries();

        this.addExecutableToEntries(-1, this.executableBlock, null, null);

        if (keepScroll) {
            this.actionsScrollArea.verticalScrollBar.setScroll(oldScrollVertical);
            this.actionsScrollArea.horizontalScrollBar.setScroll(oldScrollHorizontal);
        }

    }

    protected void addExecutableToEntries(int level, Executable executable, @Nullable AbstractExecutableBlock appendParent, @Nullable AbstractExecutableBlock parentBlock) {

        if (level >= 0) {
            ExecutableEntry entry = new ExecutableEntry(this.actionsScrollArea, executable, 14, level);
            entry.appendParent = appendParent;
            entry.parentBlock = parentBlock;
            this.actionsScrollArea.addEntry(entry);
        }

        if (executable instanceof AbstractExecutableBlock b) {
            for (Executable e : b.getExecutables()) {
                this.addExecutableToEntries(level+1, e, null, b);
            }
            if (b.getAppendedBlock() != null) {
                this.addExecutableToEntries(level, b.getAppendedBlock(), b, parentBlock);
            }
        }

    }

    public class ExecutableEntry extends ScrollAreaEntry {

        public static final int HEADER_FOOTER_HEIGHT = 3;
        public static final int INDENT_X_OFFSET = 20;

        @NotNull
        public Executable executable;
        @Nullable
        public AbstractExecutableBlock parentBlock;
        @Nullable
        public AbstractExecutableBlock appendParent;
        public final int lineHeight;
        public Font font = Minecraft.getInstance().font;
        public int indentLevel;
        public boolean leftMouseDownDragging = false;
        public double leftMouseDownDraggingPosX = 0;
        public double leftMouseDownDraggingPosY = 0;
        public boolean dragging = false;

        private final MutableComponent displayNameComponent;
        private final MutableComponent valueComponent;

        public ExecutableEntry(@NotNull ScrollArea parentScrollArea, @NotNull Executable executable, int lineHeight, int indentLevel) {

            super(parentScrollArea, 100, 15);
            this.executable = executable;
            this.lineHeight = lineHeight;
            this.indentLevel = indentLevel;

            if (this.executable instanceof ActionInstance i) {
                this.displayNameComponent = i.action.getActionDisplayName().copy().setStyle(Style.EMPTY.withColor(UIBase.getUIColorTheme().description_area_text_color.getColorInt()));
                String cachedValue = i.value;
                String valueString = ((cachedValue != null) && i.action.hasValue()) ? cachedValue : I18n.get("fancymenu.editor.action.screens.manage_screen.info.value.none");
                this.valueComponent = Component.literal(I18n.get("fancymenu.editor.action.screens.manage_screen.info.value") + " ").setStyle(Style.EMPTY.withColor(UIBase.getUIColorTheme().description_area_text_color.getColorInt())).append(Component.literal(valueString).setStyle(Style.EMPTY.withColor(UIBase.getUIColorTheme().element_label_color_normal.getColorInt())));
            } else if (this.executable instanceof IfExecutableBlock b) {
                String requirements = "";
                for (LoadingRequirementGroup g : b.body.getGroups()) {
                    if (!requirements.isEmpty()) requirements += ", ";
                    requirements += g.identifier;
                }
                for (LoadingRequirementInstance i : b.body.getInstances()) {
                    if (!requirements.isEmpty()) requirements += ", ";
                    requirements += i.requirement.getDisplayName();
                }
                this.displayNameComponent = Component.translatable("fancymenu.editor.actions.blocks.if", Component.literal(requirements)).setStyle(Style.EMPTY.withColor(UIBase.getUIColorTheme().description_area_text_color.getColorInt()));
                this.valueComponent = Component.empty();
            } else if (this.executable instanceof ElseIfExecutableBlock b) {
                String requirements = "";
                for (LoadingRequirementGroup g : b.body.getGroups()) {
                    if (!requirements.isEmpty()) requirements += ", ";
                    requirements += g.identifier;
                }
                for (LoadingRequirementInstance i : b.body.getInstances()) {
                    if (!requirements.isEmpty()) requirements += ", ";
                    requirements += i.requirement.getDisplayName();
                }
                this.displayNameComponent = Component.translatable("fancymenu.editor.actions.blocks.else_if", Component.literal(requirements)).setStyle(Style.EMPTY.withColor(UIBase.getUIColorTheme().description_area_text_color.getColorInt()));
                this.valueComponent = Component.empty();
            } else if (this.executable instanceof ElseExecutableBlock b) {
                this.displayNameComponent = Component.translatable("fancymenu.editor.actions.blocks.else").setStyle(Style.EMPTY.withColor(UIBase.getUIColorTheme().description_area_text_color.getColorInt()));
                this.valueComponent = Component.empty();
            } else {
                this.displayNameComponent = Component.literal("[UNKNOWN EXECUTABLE]").withStyle(ChatFormatting.RED);
                this.valueComponent = Component.empty();
            }

            this.setWidth(this.calculateWidth());
            this.setHeight((lineHeight * 2) + (HEADER_FOOTER_HEIGHT * 2));

        }

        @Override
        public void render(PoseStack matrix, int mouseX, int mouseY, float partial) {

            this.handleDragging();

            super.render(matrix, mouseX, mouseY, partial);

            int centerYLine1 = this.getY() + HEADER_FOOTER_HEIGHT + (this.lineHeight / 2);
            int centerYLine2 = this.getY() + HEADER_FOOTER_HEIGHT + ((this.lineHeight / 2) * 3);

            RenderSystem.enableBlend();

            int renderX = this.getX() + (INDENT_X_OFFSET * this.indentLevel);

            if (this.executable instanceof ActionInstance) {

                renderListingDot(matrix, renderX + 5, centerYLine1 - 2, UIBase.getUIColorTheme().listing_dot_color_2.getColor());
                this.font.draw(matrix, this.displayNameComponent, (float)(renderX + 5 + 4 + 3), (float)(centerYLine1 - (this.font.lineHeight / 2)), -1);

                renderListingDot(matrix, renderX + 5 + 4 + 3, centerYLine2 - 2, UIBase.getUIColorTheme().listing_dot_color_1.getColor());
                this.font.draw(matrix, this.valueComponent, (float)(renderX + 5 + 4 + 3 + 4 + 3), (float)(centerYLine2 - (this.font.lineHeight / 2)), -1);

            } else {

                renderListingDot(matrix, renderX + 5, centerYLine1 - 2, UIBase.getUIColorTheme().warning_text_color.getColor());
                this.font.draw(matrix, this.displayNameComponent, (float)(renderX + 5 + 4 + 3), (float)(centerYLine1 - (this.font.lineHeight / 2)), -1);

            }

        }

        protected void handleDragging() {
            if (!MouseInput.isLeftMouseDown()) {
                if (this.dragging) {
                    ExecutableEntry hover = ManageActionsScreen.this.renderTickDragHoveredEntry;
                    if ((hover != null) && (ManageActionsScreen.this.renderTickDraggedEntry == this)) {
                        this.getParentBlock().getExecutables().remove(this.executable);
                        int hoverIndex = Math.max(0, hover.getParentBlock().getExecutables().indexOf(hover.executable));
                        if (hover == ManageActionsScreen.this.BEFORE_FIRST) {
                            ManageActionsScreen.this.executableBlock.getExecutables().add(0, this.executable);
                        } else if (hover == ManageActionsScreen.this.AFTER_LAST) {
                            ManageActionsScreen.this.executableBlock.getExecutables().add(this.executable);
                        } else {
                            if (hover.executable instanceof AbstractExecutableBlock b) {
                                b.getExecutables().add(this.executable);
                            } else {
                                hover.getParentBlock().getExecutables().add(hoverIndex+1, this.executable);
                            }
                        }
                        ManageActionsScreen.this.updateActionInstanceScrollArea(true);
                    }
                }
                this.leftMouseDownDragging = false;
                this.dragging = false;
            }
            if (this.leftMouseDownDragging) {
                if ((this.leftMouseDownDraggingPosX != MouseInput.getMouseX()) || (this.leftMouseDownDraggingPosY != MouseInput.getMouseY())) {
                    //Only allow dragging for ActionInstances and If-Blocks
                    if (!(this.executable instanceof AbstractExecutableBlock) || (this.executable instanceof IfExecutableBlock)) {
                        this.dragging = true;
                    }
                }
            }
        }

        @NotNull
        public AbstractExecutableBlock getParentBlock() {
            if (this.parentBlock == null) {
                return ManageActionsScreen.this.executableBlock;
            }
            return this.parentBlock;
        }

        private int calculateWidth() {
            int w = 5 + 4 + 3 + this.font.width(this.displayNameComponent) + 5;
            int w2 = 5 + 4 + 3 + 4 + 3 + this.font.width(this.valueComponent) + 5;
            if (w2 > w) {
                w = w2;
            }
            w += INDENT_X_OFFSET * this.indentLevel;
            return w;
        }

        @Override
        public void onClick(ScrollAreaEntry entry) {
            if (this.parent.getEntries().contains(this)) {
                this.leftMouseDownDragging = true;
                this.leftMouseDownDraggingPosX = MouseInput.getMouseX();
                this.leftMouseDownDraggingPosY = MouseInput.getMouseY();
            }
        }

    }

}
