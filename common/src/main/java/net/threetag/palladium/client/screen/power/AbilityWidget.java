package net.threetag.palladium.client.screen.power;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityEntry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AbilityWidget extends GuiComponent {

    private static final int HEIGHT = 26;
    private static final int BOX_X = 0;
    private static final int BOX_WIDTH = 200;
    private static final int FRAME_WIDTH = 26;
    private static final int ICON_X = 8;
    private static final int ICON_Y = 5;
    private static final int ICON_WIDTH = 26;
    private static final int TITLE_PADDING_LEFT = 3;
    private static final int TITLE_PADDING_RIGHT = 5;
    private static final int TITLE_X = 32;
    private static final int TITLE_Y = 9;
    private static final int TITLE_MAX_WIDTH = 163;
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private final PowerTab tab;
    private final IPowerHolder holder;
    public final AbilityEntry abilityEntry;
    private final FormattedCharSequence title;
    private final int width;
    private final List<FormattedCharSequence> description;
    private final Minecraft minecraft;
    List<AbilityWidget> parents = new LinkedList<>();
    List<AbilityWidget> children = new LinkedList<>();
    private int x;
    private int y;
    public double gridX, gridY;
    public boolean fixedPosition = false;

    public AbilityWidget(PowerTab tab, Minecraft mc, IPowerHolder holder, AbilityEntry abilityEntry) {
        this.tab = tab;
        this.holder = holder;
        this.abilityEntry = abilityEntry;
        this.minecraft = mc;
        this.title = Language.getInstance().getVisualOrder(mc.font.substrByWidth(abilityEntry.getConfiguration().getDisplayName(), 163));
        int l = 29 + mc.font.width(this.title);
        var description = abilityEntry.getProperty(Ability.DESCRIPTION);
        this.description = Language.getInstance()
                .getVisualOrder(
                        this.findOptimalLines(ComponentUtils.mergeStyles(description != null ? description.copy() : Component.empty(), Style.EMPTY.withColor(ChatFormatting.WHITE)), l)
                );

        for (FormattedCharSequence formattedCharSequence : this.description) {
            l = Math.max(l, minecraft.font.width(formattedCharSequence));
        }

        this.width = l + 3 + 5;
    }

    public AbilityWidget updatePosition(double x, double y, PowerTab tab) {
        this.gridX = x;
        this.gridY = y;
        this.x = (int) (x * PowerTab.GRID_SIZE) - 16;
        this.y = (int) (tab.getFreeYPos(x, y) * PowerTab.GRID_SIZE) - 13;

        for (AbilityWidget child : this.children) {
            child.updatePosition(this.gridX + 1, y, tab);
        }

        return this;
    }

    public AbilityWidget setPosition(double x, double y) {
        this.gridX = x;
        this.gridY = y;
        this.x = (int) (x * PowerTab.GRID_SIZE) - 16;
        this.y = (int) (y * PowerTab.GRID_SIZE) - 13;
        return this;
    }

    public AbilityWidget setPositionFixed(double x, double y) {
        this.fixedPosition = true;
        return this.setPosition(x, y);
    }

    public AbilityWidget updateRelatives(Collection<AbilityWidget> list) {
        this.parents.clear();
        this.children.clear();
        List<AbilityEntry> parents = Ability.findParentAbilities(this.minecraft.player, this.abilityEntry.getConfiguration(), this.holder);
        List<AbilityEntry> children = Ability.findChildrenAbilities(this.minecraft.player, this.abilityEntry.getConfiguration(), this.holder);

        for (AbilityWidget widget : list) {
            if (!parents.isEmpty()) {
                if (parents.contains(widget.abilityEntry)) {
                    this.parents.add(widget);
                }
            }

            if (!children.isEmpty()) {
                if (children.contains(widget.abilityEntry)) {
                    this.children.add(widget);
                }
            }
        }

        return this;
    }

    private static float getMaxWidth(StringSplitter manager, List<FormattedText> text) {
        return (float)text.stream().mapToDouble(manager::stringWidth).max().orElse(0.0);
    }

    private List<FormattedText> findOptimalLines(Component component, int maxWidth) {
        StringSplitter stringSplitter = this.minecraft.font.getSplitter();
        List<FormattedText> list = null;
        float f = Float.MAX_VALUE;

        for(int i : TEST_SPLIT_OFFSETS) {
            List<FormattedText> list2 = stringSplitter.splitLines(component, maxWidth - i, Style.EMPTY);
            float g = Math.abs(getMaxWidth(stringSplitter, list2) - (float)maxWidth);
            if (g <= 10.0F) {
                return list2;
            }

            if (g < f) {
                f = g;
                list = list2;
            }
        }

        return list;
    }

    public void drawDisplayIcon(Minecraft mc, PoseStack stack, int x, int y) {
        if (this.abilityEntry.isUnlocked()) {
            this.abilityEntry.getProperty(Ability.ICON).draw(mc, stack, x, y);
        } else {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, PowersScreen.WIDGETS);
            this.blit(stack, x, y, 90, 133, 16, 16);
        }
    }

    public void drawIcon(Minecraft mc, PoseStack stack, int x, int y) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PowersScreen.WIDGETS);
        this.blit(stack, x - 13, y - 13, 0, this.abilityEntry.isUnlocked() ? 128 : 154, 26, 26);
        this.drawDisplayIcon(mc, stack, x - 8, y - 8);
    }

    public int getWidth() {
        return this.width;
    }

    public void drawHover(PoseStack poseStack, int x, int y, float fade, int width, int height) {
        boolean bl = width + x + this.x + this.width + 26 >= this.tab.getScreen().width;
        String string = null;
        int i = string == null ? 0 : this.minecraft.font.width(string);
        boolean bl2 = 113 - y - this.y - 26 <= 6 + this.description.size() * 9;
        float f = 0.0F;
        int j = Mth.floor(f * (float)this.width);
        AdvancementWidgetType advancementWidgetType;
        AdvancementWidgetType advancementWidgetType2;
        AdvancementWidgetType advancementWidgetType3;
        if (f >= 1.0F) {
            j = this.width / 2;
            advancementWidgetType = AdvancementWidgetType.OBTAINED;
            advancementWidgetType2 = AdvancementWidgetType.OBTAINED;
            advancementWidgetType3 = AdvancementWidgetType.OBTAINED;
        } else if (j < 2) {
            j = this.width / 2;
            advancementWidgetType = AdvancementWidgetType.UNOBTAINED;
            advancementWidgetType2 = AdvancementWidgetType.UNOBTAINED;
            advancementWidgetType3 = AdvancementWidgetType.UNOBTAINED;
        } else if (j > this.width - 2) {
            j = this.width / 2;
            advancementWidgetType = AdvancementWidgetType.OBTAINED;
            advancementWidgetType2 = AdvancementWidgetType.OBTAINED;
            advancementWidgetType3 = AdvancementWidgetType.UNOBTAINED;
        } else {
            advancementWidgetType = AdvancementWidgetType.OBTAINED;
            advancementWidgetType2 = AdvancementWidgetType.UNOBTAINED;
            advancementWidgetType3 = AdvancementWidgetType.UNOBTAINED;
        }

        int k = this.width - j;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PowersScreen.WIDGETS);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        int l = y + this.y;
        int m;
        if (bl) {
            m = x + this.x - this.width + 26 + 6;
        } else {
            m = x + this.x;
        }

        int n = 32 + this.description.size() * 9;
        if (!this.description.isEmpty()) {
            if (bl2) {
                this.render9Sprite(poseStack, m, l + 26 - n, this.width, n, 10, 200, 26, 0, 52);
            } else {
                this.render9Sprite(poseStack, m, l, this.width, n, 10, 200, 26, 0, 52);
            }
        }

        this.blit(poseStack, m, l, 0, advancementWidgetType.getIndex() * 26, j, 26);
        this.blit(poseStack, m + j, l, 200 - k, advancementWidgetType2.getIndex() * 26, k, 26);
        this.blit(poseStack, x + this.x + 3, y + this.y, 0, 128 + advancementWidgetType3.getIndex() * 26, 26, 26);
        if (bl) {
            this.minecraft.font.drawShadow(poseStack, this.title, (float)(m + 5), (float)(y + this.y + 9), -1);
            if (string != null) {
                this.minecraft.font.drawShadow(poseStack, string, (float)(x + this.x - i), (float)(y + this.y + 9), -1);
            }
        } else {
            this.minecraft.font.drawShadow(poseStack, this.title, (float)(x + this.x + 32), (float)(y + this.y + 9), -1);
            if (string != null) {
                this.minecraft.font.drawShadow(poseStack, string, (float)(x + this.x + this.width - i - 5), (float)(y + this.y + 9), -1);
            }
        }

        if (bl2) {
            for(int o = 0; o < this.description.size(); ++o) {
                this.minecraft.font.draw(poseStack, this.description.get(o), (float)(m + 5), (float)(l + 26 - n + 7 + o * 9), -5592406);
            }
        } else {
            for(int o = 0; o < this.description.size(); ++o) {
                this.minecraft.font.draw(poseStack, this.description.get(o), (float)(m + 5), (float)(y + this.y + 9 + 17 + o * 9), -5592406);
            }
        }

        this.drawDisplayIcon(this.minecraft, poseStack, x + this.x + 8, y + this.y + 5);
    }

    protected void render9Sprite(PoseStack poseStack, int x, int y, int width, int height, int padding, int uWidth, int vHeight, int uOffset, int vOffset) {
        this.blit(poseStack, x, y, uOffset, vOffset, padding, padding);
        this.renderRepeating(poseStack, x + padding, y, width - padding - padding, padding, uOffset + padding, vOffset, uWidth - padding - padding, vHeight);
        this.blit(poseStack, x + width - padding, y, uOffset + uWidth - padding, vOffset, padding, padding);
        this.blit(poseStack, x, y + height - padding, uOffset, vOffset + vHeight - padding, padding, padding);
        this.renderRepeating(
                poseStack,
                x + padding,
                y + height - padding,
                width - padding - padding,
                padding,
                uOffset + padding,
                vOffset + vHeight - padding,
                uWidth - padding - padding,
                vHeight
        );
        this.blit(poseStack, x + width - padding, y + height - padding, uOffset + uWidth - padding, vOffset + vHeight - padding, padding, padding);
        this.renderRepeating(poseStack, x, y + padding, padding, height - padding - padding, uOffset, vOffset + padding, uWidth, vHeight - padding - padding);
        this.renderRepeating(
                poseStack,
                x + padding,
                y + padding,
                width - padding - padding,
                height - padding - padding,
                uOffset + padding,
                vOffset + padding,
                uWidth - padding - padding,
                vHeight - padding - padding
        );
        this.renderRepeating(
                poseStack,
                x + width - padding,
                y + padding,
                padding,
                height - padding - padding,
                uOffset + uWidth - padding,
                vOffset + padding,
                uWidth,
                vHeight - padding - padding
        );
    }

    protected void renderRepeating(PoseStack poseStack, int x, int y, int borderToU, int borderToV, int uOffset, int vOffset, int uWidth, int vHeight) {
        for(int i = 0; i < borderToU; i += uWidth) {
            int j = x + i;
            int k = Math.min(uWidth, borderToU - i);

            for(int l = 0; l < borderToV; l += vHeight) {
                int m = y + l;
                int n = Math.min(vHeight, borderToV - l);
                this.blit(poseStack, j, m, uOffset, vOffset, k, n);
            }
        }
    }

    public boolean isMouseOver(int x, int y, int mouseX, int mouseY) {
        int i = x + this.x;
        int j = i + 26;
        int k = y + this.y;
        int l = k + 26;
        return mouseX >= i && mouseX <= j && mouseY >= k && mouseY <= l;
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

}
