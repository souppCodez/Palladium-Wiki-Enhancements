package com.threetag.threecore.abilities.capability;

import com.threetag.threecore.abilities.IAbilityContainer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AbilityContainerProvider implements ICapabilitySerializable<CompoundNBT> {

    public final IAbilityContainer container;

    public AbilityContainerProvider(IAbilityContainer container) {
        this.container = container;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityAbilityContainer.ABILITY_CONTAINER ? LazyOptional.of(() -> (T) this.container) : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (this.container instanceof INBTSerializable)
            return ((INBTSerializable<CompoundNBT>) this.container).serializeNBT();
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (this.container instanceof INBTSerializable)
            ((INBTSerializable<CompoundNBT>) this.container).deserializeNBT(nbt);
    }
}
