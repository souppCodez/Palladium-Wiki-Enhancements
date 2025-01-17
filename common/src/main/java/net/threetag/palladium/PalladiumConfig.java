package net.threetag.palladium;

import net.minecraftforge.common.ForgeConfigSpec;
import net.threetag.palladium.client.screen.AbilityBarRenderer;

public class PalladiumConfig {

    public static class Client {

        public static ForgeConfigSpec.EnumValue<AbilityBarRenderer.Position> ABILITY_BAR_POSITION;
        public static ForgeConfigSpec.BooleanValue ADDON_PACK_DEV_MODE;

        public static ForgeConfigSpec generateConfig() {
            ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
            ABILITY_BAR_POSITION = builder.defineEnum("abilityBarPosition", AbilityBarRenderer.Position.BOTTOM_RIGHT);
            ADDON_PACK_DEV_MODE = builder.define("addonPackDevMode", false);
            return builder.build();
        }

    }

}
