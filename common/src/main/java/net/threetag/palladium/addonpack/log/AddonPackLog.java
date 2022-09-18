package net.threetag.palladium.addonpack.log;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.PalladiumConfig;
import net.threetag.palladium.client.screen.AddonPackLogScreen;
import net.threetag.palladiumcore.event.ScreenEvents;
import org.apache.logging.log4j.message.FormattedMessage;
import org.apache.logging.log4j.message.Message;

import java.util.ArrayList;
import java.util.List;

public class AddonPackLog {

    private static final List<AddonPackLogEntry> ENTRIES = new ArrayList<>();

    public static List<AddonPackLogEntry> getEntries() {
        return ENTRIES;
    }

    public static void info(String message, Object... params) {
        Palladium.LOGGER.info(message, params);
        final Message msg = new FormattedMessage(message, params);
        ENTRIES.add(new AddonPackLogEntry(AddonPackLogEntry.Type.INFO, msg));
    }

    public static void error(String message, Object... params) {
        Palladium.LOGGER.error(message, params);
        final Message msg = new FormattedMessage(message, params);
        ENTRIES.add(new AddonPackLogEntry(AddonPackLogEntry.Type.ERROR, msg));
    }

    public static void error(Exception exception, String message, Object... params) {
        Palladium.LOGGER.error(message, params);
        final Message msg = new FormattedMessage(message + "\n" + exception.getMessage(), params);
        ENTRIES.add(new AddonPackLogEntry(AddonPackLogEntry.Type.ERROR, msg, exception.getStackTrace()));
    }

    public static void warning(String message, Object... params) {
        Palladium.LOGGER.warn(message, params);
        final Message msg = new FormattedMessage(message, params);
        ENTRIES.add(new AddonPackLogEntry(AddonPackLogEntry.Type.WARNING, msg));
    }

    public static void warning(Exception exception, String message, Object... params) {
        Palladium.LOGGER.warn(message, params);
        final Message msg = new FormattedMessage(message + "\n" + exception.getMessage(), params);
        ENTRIES.add(new AddonPackLogEntry(AddonPackLogEntry.Type.WARNING, msg, exception.getStackTrace()));
    }

    public static void setupButton() {
        ScreenEvents.INIT_POST.register((screen) -> {
            if (PalladiumConfig.Client.ADDON_PACK_DEV_MODE.get() && screen instanceof TitleScreen) {
                screen.addRenderableWidget(new Button(10, 10, 200, 20, Component.translatable("gui.palladium.addon_pack_log"), (p_213079_1_) -> {
                    Minecraft.getInstance().setScreen(new AddonPackLogScreen(AddonPackLog.getEntries(), screen));
                }));
            }
        });
    }
}
