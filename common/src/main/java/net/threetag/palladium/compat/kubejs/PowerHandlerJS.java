package net.threetag.palladium.compat.kubejs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.threetag.palladium.power.Power;
import net.threetag.palladium.power.PowerManager;
import net.threetag.palladium.util.property.PalladiumProperties;

import java.util.*;

public class PowerHandlerJS {

    private final Player parent;

    public PowerHandlerJS(Player parent) {
        this.parent = parent;
    }

    public Collection<ResourceLocation> getPowers() {
        var handler = PowerManager.getPowerHandler(this.parent);
        return handler.isPresent() ? handler.get().getPowerHolders().keySet() : Collections.emptyList();
    }

    public boolean setSuperpower(ResourceLocation id) {
        Power power = PowerManager.getInstance(Objects.requireNonNull(this.parent).level).getPower(id);

        if (power != null) {
            PalladiumProperties.SUPERPOWER_IDS.set(this.parent, Collections.singletonList(id));
            return true;
        } else {
            return false;
        }
    }

    public boolean addSuperpower(ResourceLocation id) {
        Power power = PowerManager.getInstance(Objects.requireNonNull(this.parent).level).getPower(id);

        if (power != null && !PalladiumProperties.SUPERPOWER_IDS.get(this.parent).contains(id)) {
            List<ResourceLocation> powerIds = new ArrayList<>(PalladiumProperties.SUPERPOWER_IDS.get(this.parent));
            powerIds.add(id);
            PalladiumProperties.SUPERPOWER_IDS.set(this.parent, powerIds);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeSuperpower(ResourceLocation id) {
        Power power = PowerManager.getInstance(Objects.requireNonNull(this.parent).level).getPower(id);

        if (power != null && PalladiumProperties.SUPERPOWER_IDS.get(this.parent).contains(id)) {
            List<ResourceLocation> powerIds = new ArrayList<>(PalladiumProperties.SUPERPOWER_IDS.get(this.parent));
            powerIds.remove(id);
            PalladiumProperties.SUPERPOWER_IDS.set(this.parent, powerIds);
            return true;
        } else {
            return false;
        }
    }

    public List<ResourceLocation> getSuperpower() {
        return PalladiumProperties.SUPERPOWER_IDS.get(this.parent);
    }

}
