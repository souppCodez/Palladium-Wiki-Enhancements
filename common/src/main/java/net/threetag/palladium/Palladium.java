package net.threetag.palladium;

import net.minecraft.resources.ResourceLocation;
import net.threetag.palladium.accessory.Accessories;
import net.threetag.palladium.addonpack.AddonPackManager;
import net.threetag.palladium.addonpack.parser.ArmorMaterialParser;
import net.threetag.palladium.addonpack.parser.CreativeModeTabParser;
import net.threetag.palladium.addonpack.parser.ItemParser;
import net.threetag.palladium.addonpack.parser.ToolTierParser;
import net.threetag.palladium.block.PalladiumBlocks;
import net.threetag.palladium.block.entity.PalladiumBlockEntityTypes;
import net.threetag.palladium.command.SuperpowerCommand;
import net.threetag.palladium.compat.pehkui.PehkuiCompat;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.condition.ConditionSerializers;
import net.threetag.palladium.documentation.HTMLBuilder;
import net.threetag.palladium.entity.FlightHandler;
import net.threetag.palladium.entity.PalladiumAttributes;
import net.threetag.palladium.entity.PalladiumEntityTypes;
import net.threetag.palladium.entity.effect.EntityEffects;
import net.threetag.palladium.event.PalladiumEvents;
import net.threetag.palladium.item.PalladiumItems;
import net.threetag.palladium.network.PalladiumNetwork;
import net.threetag.palladium.power.ItemPowerManager;
import net.threetag.palladium.power.PowerManager;
import net.threetag.palladium.power.SuitSetPowerManager;
import net.threetag.palladium.power.ability.Abilities;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.power.ability.AbilityEventHandler;
import net.threetag.palladium.power.provider.PowerProviders;
import net.threetag.palladium.sound.PalladiumSoundEvents;
import net.threetag.palladium.util.SupporterHandler;
import net.threetag.palladium.util.icon.IconSerializer;
import net.threetag.palladium.util.icon.IconSerializers;
import net.threetag.palladium.util.property.EntityPropertyHandler;
import net.threetag.palladium.util.property.PalladiumProperties;
import net.threetag.palladiumcore.event.CommandEvents;
import net.threetag.palladiumcore.event.LifecycleEvents;
import net.threetag.palladiumcore.util.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class Palladium {

    public static final String MOD_ID = "palladium";
    public static final Logger LOGGER = LogManager.getLogger();

    public static void init() {
        PalladiumBlocks.BLOCKS.register();
        PalladiumBlockEntityTypes.BLOCK_ENTITIES.register();
        PalladiumItems.ITEMS.register();
        Abilities.ABILITIES.register();
        ConditionSerializers.CONDITION_SERIALIZERS.register();
        PowerProviders.PROVIDERS.register();
        IconSerializers.ICON_SERIALIZERS.register();
//        PalladiumFeatures.FEATURES.register();
        PalladiumAttributes.ATTRIBUTES.register();
        EntityEffects.EFFECTS.register();
        PalladiumEntityTypes.ENTITIES.register();
        PalladiumSoundEvents.SOUNDS.register();
        Accessories.ACCESSORIES.register();

        PalladiumNetwork.init();
        EntityPropertyHandler.init();
        PowerManager.init();
        ItemPowerManager.init();
        SuitSetPowerManager.init();
        AbilityEventHandler.init();
        AddonPackManager.init();
        Abilities.init();
        PalladiumProperties.init();
        PalladiumAttributes.init();
        FlightHandler.init();
        EntityEffects.init();
        SupporterHandler.init();

        LifecycleEvents.SETUP.register(() -> {
//            PalladiumFeatures.init();
            Palladium.generateDocumentation();

            if (Platform.isModLoaded("pehkui")) {
                PehkuiCompat.init();
            }
        });

        CommandEvents.REGISTER.register((dispatcher, selection) -> SuperpowerCommand.register(dispatcher));

        if (!Platform.isProduction()) {
            PalladiumDebug.init();
        }
    }

    public static void generateDocumentation() {
        if (Platform.isClient()) {
            Consumer<HTMLBuilder> consumer = HTMLBuilder::save;
            consumer.accept(Ability.documentationBuilder());
            consumer.accept(ConditionSerializer.documentationBuilder());
            consumer.accept(CreativeModeTabParser.documentationBuilder());
            consumer.accept(ArmorMaterialParser.documentationBuilder());
            consumer.accept(ToolTierParser.documentationBuilder());
            consumer.accept(ItemParser.documentationBuilder());
            consumer.accept(IconSerializer.documentationBuilder());
            PalladiumEvents.GENERATE_DOCUMENTATION.invoker().generate(consumer);
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
