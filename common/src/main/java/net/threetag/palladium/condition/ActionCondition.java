package net.threetag.palladium.condition;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.LivingEntity;
import net.threetag.palladium.power.IPowerHolder;
import net.threetag.palladium.power.Power;
import net.threetag.palladium.power.ability.AbilityConfiguration;
import net.threetag.palladium.power.ability.AbilityEntry;
import net.threetag.palladium.util.property.IntegerProperty;
import net.threetag.palladium.util.property.PalladiumProperty;

import java.util.Objects;

public class ActionCondition extends KeyCondition {

    public ActionCondition(int cooldown, AbilityConfiguration.KeyType type) {
        super(cooldown, type);
    }

    @Override
    public boolean active(LivingEntity entity, AbilityEntry entry, Power power, IPowerHolder holder) {
        if (Objects.requireNonNull(entry).keyPressed) {
            entry.keyPressed = false;
            return true;
        }
        return false;
    }

    @Override
    public void onKeyPressed(LivingEntity entity, AbilityEntry entry, Power power, IPowerHolder holder) {
        if (entry.cooldown == 0) {
            entry.keyPressed = true;

            if (this.cooldown != 0) {
                entry.startCooldown(entity, this.cooldown);
            }
        }
    }

    @Override
    public ConditionSerializer getSerializer() {
        return ConditionSerializers.ACTION.get();
    }

    public static class Serializer extends ConditionSerializer {

        public static final PalladiumProperty<Integer> COOLDOWN = new IntegerProperty("cooldown").configurable("Amount of ticks the ability wont be useable for after using it");

        public Serializer() {
            this.withProperty(COOLDOWN, 0);
            this.withProperty(KeyCondition.KEY_TYPE, AbilityConfiguration.KeyType.KEY_BIND);
        }

        @Override
        public Condition make(JsonObject json) {
            return new ActionCondition(this.getProperty(json, COOLDOWN), this.getProperty(json, KeyCondition.KEY_TYPE));
        }

        @Override
        public ConditionContextType getContextType() {
            return ConditionContextType.ABILITIES;
        }
    }

}
