package net.threetag.palladium.fabric;

import dev.architectury.platform.Platform;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.threetag.palladium.Palladium;
import net.threetag.palladium.block.entity.PalladiumBlockEntityTypes;
import net.threetag.palladium.compat.trinkets.fabric.TrinketsCompat;
import net.threetag.palladium.energy.IBlockEntityEnergyContainer;
import team.reborn.energy.api.EnergyStorage;

public class PalladiumFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Palladium.init();
        AutoConfig.register(PalladiumConfigImpl.class, Toml4jConfigSerializer::new);

        if (Platform.isModLoaded("trinkets")) {
            TrinketsCompat.init();
        }

        registerEnergyHandlers();
    }

    private static void registerEnergyHandlers() {
        EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> (EnergyStorage) ((IBlockEntityEnergyContainer) blockEntity).getEnergyStorage(direction), PalladiumBlockEntityTypes.SOLAR_PANEL.get());
    }

}