package net.threetag.palladium.power.ability;

import net.minecraft.resources.ResourceLocation;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.util.icon.TexturedIcon;
import net.threetag.palladiumcore.registry.DeferredRegister;
import net.threetag.palladiumcore.registry.RegistrySupplier;

public class Abilities {

    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(Palladium.MOD_ID, Ability.REGISTRY);

    public static final RegistrySupplier<Ability> DUMMY = ABILITIES.register("dummy", Ability::new);
    public static final RegistrySupplier<Ability> COMMAND = ABILITIES.register("command", CommandAbility::new);
    public static final RegistrySupplier<Ability> RENDER_LAYER = ABILITIES.register("render_layer", RenderLayerAbility::new);
    public static final RegistrySupplier<Ability> INTERPOLATED_INTEGER = ABILITIES.register("interpolated_integer", InterpolatedIntegerAbility::new);
    public static final RegistrySupplier<Ability> SHRINK_BODY_OVERLAY = ABILITIES.register("shrink_body_overlay", ShrinkBodyOverlayAbility::new);
    public static final RegistrySupplier<Ability> ATTRIBUTE_MODIFIER = ABILITIES.register("attribute_modifier", AttributeModifierAbility::new);
    public static final RegistrySupplier<Ability> HEALING = ABILITIES.register("healing", HealingAbility::new);
    public static final RegistrySupplier<Ability> SLOWFALL = ABILITIES.register("slowfall", SlowfallAbility::new);
    public static final RegistrySupplier<Ability> DAMAGE_IMMUNITY = ABILITIES.register("damage_immunity", DamageImmunityAbility::new);
    public static final RegistrySupplier<Ability> INVISIBILITY = ABILITIES.register("invisibility", () -> new Ability().withProperty(Ability.ICON, new TexturedIcon(new ResourceLocation(Palladium.MOD_ID, "textures/icon/invisibility.png"))));
    public static final RegistrySupplier<Ability> ENERGY_BLAST = ABILITIES.register("energy_blast", EnergyBlastAbility::new);
    public static final RegistrySupplier<Ability> SIZE = ABILITIES.register("size", SizeAbility::new);
    public static final RegistrySupplier<Ability> PROJECTILE = ABILITIES.register("projectile", ProjectileAbility::new);
    public static final RegistrySupplier<Ability> SKIN_CHANGE = ABILITIES.register("skin_change", SkinChangeAbility::new);
    public static final RegistrySupplier<Ability> AIM = ABILITIES.register("aim", AimAbility::new);
    public static final RegistrySupplier<Ability> HIDE_BODY_PARTS = ABILITIES.register("hide_body_parts", HideBodyPartsAbility::new);
    public static final RegistrySupplier<Ability> SHADER_EFFECT = ABILITIES.register("shader_effect", ShaderEffectAbility::new);
    public static final RegistrySupplier<Ability> GUI_OVERLAY = ABILITIES.register("gui_overlay", GuiOverlayAbility::new);
    public static final RegistrySupplier<Ability> SHOW_BOTH_ARMS = ABILITIES.register("show_both_arms", () -> new Ability().withProperty(Ability.HIDDEN, true));

    public static void init() {

    }

}
