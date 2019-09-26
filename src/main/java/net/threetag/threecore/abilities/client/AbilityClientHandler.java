package net.threetag.threecore.abilities.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.threetag.threecore.ThreeCore;
import net.threetag.threecore.abilities.Ability;
import net.threetag.threecore.abilities.AbilityHelper;
import net.threetag.threecore.abilities.ColorHeartsAbility;
import net.threetag.threecore.abilities.InvisibilityAbility;
import net.threetag.threecore.abilities.client.gui.AbilitiesScreen;
import net.threetag.threecore.abilities.client.renderer.AbilityBarRenderer;
import net.threetag.threecore.abilities.network.AbilityKeyMessage;
import net.threetag.threecore.util.client.gui.TranslucentButton;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AbilityClientHandler {

    // TODO Add key modifiers to custom keybindings

    public static final String CATEGORY = "ThreeCore";
    public static final KeyBinding SCROLL_UP = new KeyBinding("key.threecore.scroll_up", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, 73, CATEGORY);
    public static final KeyBinding SCROLL_DOWN = new KeyBinding("key.threecore.scroll_down", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, 80, CATEGORY);

    public AbilityClientHandler() {
        if (Minecraft.getInstance() != null) {
            ClientRegistry.registerKeyBinding(SCROLL_UP);
            ClientRegistry.registerKeyBinding(SCROLL_DOWN);
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().currentScreen != null)
            return;

        if (e.getAction() < GLFW.GLFW_REPEAT) {
            List<Ability> abilities = AbilityHelper.getAbilities(Minecraft.getInstance().player).stream().filter(a -> a.getConditionManager().isUnlocked() && a.getConditionManager().needsKey() && a.getDataManager().get(Ability.KEYBIND) > -1).collect(Collectors.toList());

            for (Ability ability : abilities) {
                if (ability.getDataManager().get(Ability.KEYBIND) == e.getKey())
                    ThreeCore.NETWORK_CHANNEL.sendToServer(new AbilityKeyMessage(ability.getContainer().getId(), ability.getId(), e.getAction() == GLFW.GLFW_PRESS));
            }
        }

        if (SCROLL_UP.isKeyDown()) {
            AbilityBarRenderer.scroll(true);
        } else if (SCROLL_DOWN.isKeyDown()) {
            AbilityBarRenderer.scroll(false);
        }
    }

    @SubscribeEvent
    public void onMouse(GuiScreenEvent.MouseScrollEvent.Pre e) {
        if (Minecraft.getInstance().player == null)
            return;

        if (Minecraft.getInstance().player.isSneaking() && e.getScrollDelta() != 0F) {
            AbilityBarRenderer.scroll(e.getScrollDelta() > 0);
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent e) {
        if (e.getGui() instanceof ChatScreen) {
            e.addWidget(new TranslucentButton(e.getGui().width - 1 - 75, e.getGui().height - 40, 75, 20, I18n.format("gui.threecore.abilities"), b -> Minecraft.getInstance().displayGuiScreen(new AbilitiesScreen())));
        }
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre e) {
        for (InvisibilityAbility invisibilityAbility : AbilityHelper.getAbilitiesFromClass(e.getEntity(), InvisibilityAbility.class)) {
            if (invisibilityAbility.getConditionManager().isEnabled()) {
                e.setCanceled(true);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onHeartsPre(RenderGameOverlayEvent.Pre e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            for (ColorHeartsAbility ability : AbilityHelper.getAbilitiesFromClass(Minecraft.getInstance().player, ColorHeartsAbility.class)) {
                if (ability.getConditionManager().isEnabled()) {
                    Color color = ability.getDataManager().get(ColorHeartsAbility.COLOR);
                    GlStateManager.color3f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
                }
            }
        }
    }

    @SubscribeEvent
    public void onHeartsPost(RenderGameOverlayEvent.Post e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            GlStateManager.color4f(1F, 1F, 1F, 1F);
        }
    }

}
