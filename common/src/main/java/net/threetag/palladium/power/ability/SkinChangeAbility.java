package net.threetag.palladium.power.ability;

import net.minecraft.resources.ResourceLocation;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.ResourceLocationProperty;

public class SkinChangeAbility extends Ability {

    public static final PalladiumProperty<ResourceLocation> TEXTURE = new ResourceLocationProperty("texture").configurable("Texture for the skin change");
    public static final PalladiumProperty<Integer> PRIORITY = new IntegerProperty("priority").configurable("Priority for the skin (in case multiple skin changes are applied, the one with the highest priority will be used)");

    public SkinChangeAbility() {
        this.withProperty(TEXTURE, new ResourceLocation("textures/entity/zombie/drowned.png"));
        this.withProperty(PRIORITY, 50);
    }

    @Override
    public boolean isEffect() {
        return true;
    }
}