package net.threetag.threecore.util.scripts.events;

import net.minecraft.entity.LivingEntity;
import net.threetag.threecore.abilities.Ability;

public class AbilityLockedScriptEvent extends AbilityScriptEvent {

    public AbilityLockedScriptEvent(LivingEntity livingEntity, Ability ability) {
        super(livingEntity, ability);
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}