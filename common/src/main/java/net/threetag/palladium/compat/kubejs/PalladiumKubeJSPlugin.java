package net.threetag.palladium.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.player.PlayerDataJS;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.threetag.palladium.client.model.animation.AnimationUtil;
import net.threetag.palladium.compat.kubejs.ability.AbilityBuilder;
import net.threetag.palladium.compat.kubejs.condition.ConditionBuilder;
import net.threetag.palladium.condition.ConditionSerializer;
import net.threetag.palladium.event.PalladiumClientEvents;
import net.threetag.palladium.event.PalladiumEvents;
import net.threetag.palladium.power.ability.Ability;
import net.threetag.palladium.util.property.*;

public class PalladiumKubeJSPlugin extends KubeJSPlugin {

    public static RegistryObjectBuilderTypes<Ability> ABILITY;
    public static RegistryObjectBuilderTypes<ConditionSerializer> CONDITION;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void init() {
        PalladiumEvents.REGISTER_PROPERTY.register(handler -> new RegisterPalladiumPropertyEventJS(handler.getEntity(), handler).post(ScriptType.of(handler.getEntity().level), PalladiumJSEvents.REGISTER_PROPERTIES));

        ResourceKey key = Ability.REGISTRY.getRegistryKey();
        ABILITY = RegistryObjectBuilderTypes.add(key, Ability.class);
        ABILITY.addType("basic", AbilityBuilder.class, AbilityBuilder::new);

        key = ConditionSerializer.REGISTRY.getRegistryKey();
        CONDITION = RegistryObjectBuilderTypes.add(key, ConditionSerializer.class);
        CONDITION.addType("basic", ConditionBuilder.class, ConditionBuilder::new);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void clientInit() {
        PalladiumClientEvents.REGISTER_ANIMATIONS.register(registry -> new RegisterAnimationsEventJS(registry).post(ScriptType.CLIENT, PalladiumJSEvents.REGISTER_ANIMATIONS));
    }

    @Override
    public void addBindings(BindingsEvent event) {
        event.add("palladium", PalladiumBinding.class);
        event.add("animationUtil", AnimationUtil.class);
    }

    @Override
    public void attachPlayerData(AttachDataEvent<PlayerDataJS> event) {
        event.add("powers", new PowerHandlerJS(event.parent()));
        event.add("properties", new PalladiumPropertyHandlerJS(event.parent()));
    }

    @Override
    public void attachLevelData(AttachDataEvent<LevelJS> event) {
        event.add("powers", new PowerManagerJS(event.parent()));
    }

    public static Object fixValues(PalladiumProperty<?> property, Object value) {
        if (property instanceof IntegerProperty && value instanceof Number number) {
            value = number.intValue();
        } else if (property instanceof FloatProperty && value instanceof Number number) {
            value = number.floatValue();
        } else if (property instanceof DoubleProperty && value instanceof Number number) {
            value = number.doubleValue();
        } else if (property instanceof ResourceLocationProperty && value instanceof String string) {
            value = new ResourceLocation(string);
        }

        return value;
    }

}
