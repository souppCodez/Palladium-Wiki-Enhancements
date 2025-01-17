package net.threetag.palladium.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.Power;
import net.threetag.palladium.power.ability.AbilityConfiguration;
import net.threetag.palladium.power.ability.AbilityEntry;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;
import net.threetag.palladium.util.property.PropertyManager;

public class HeldCondition extends KeyCondition {

    public HeldCondition(int cooldown, AbilityConfiguration.KeyType type) {
        super(cooldown, type);
    }

    @Override
    public void init(LivingEntity entity, AbilityEntry entry, PropertyManager manager) {
        entry.startCooldown(entity, this.cooldown);
    }

    @Override
    public boolean active(LivingEntity entity, AbilityEntry entry, Power power, IPowerHolder holder) {
        if (this.cooldown != 0 && entry.cooldown == 0 && entry.keyPressed) {
            entry.keyPressed = false;
        }
        return entry.keyPressed;
    }

    @Override
    public void onKeyPressed(LivingEntity entity, AbilityEntry entry, Power power, IPowerHolder holder) {
        entry.keyPressed = true;
    }

    @Override
    public void onKeyReleased(LivingEntity entity, AbilityEntry entry, Power power, IPowerHolder holder) {
        entry.keyPressed = false;
    }

    @Override
    public CooldownType getCooldownType() {
        return CooldownType.DYNAMIC;
    }

    @Override
    public ConditionSerializer getSerializer() {
        return ConditionSerializers.HELD.get();
    }

    public static class Serializer extends ConditionSerializer {

        public static final PalladiumProperty<Integer> COOLDOWN = new IntegerProperty("cooldown").configurable("Amount of ticks the ability can be used for");

        public Serializer() {
            this.withProperty(COOLDOWN, 0);
            this.withProperty(KeyCondition.KEY_TYPE, AbilityConfiguration.KeyType.KEY_BIND);
        }

        @Override
        public Condition make(JsonObject json) {
            return new HeldCondition(this.getProperty(json, COOLDOWN), this.getProperty(json, KeyCondition.KEY_TYPE));
        }

        @Override
        public ConditionContextType getContextType() {
            return ConditionContextType.ABILITIES;
        }
    }

}
