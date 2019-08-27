package com.threetag.threecore.base.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.threetag.threecore.base.ThreeCoreBase;
import com.threetag.threecore.base.block.HydraulicPressBlock;
import com.threetag.threecore.base.inventory.HydraulicPressContainer;
import com.threetag.threecore.base.recipe.PressingRecipe;
import com.threetag.threecore.util.energy.EnergyStorageExt;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HydraulicPressTileEntity extends TileEntity implements IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity, INamedContainerProvider, INameable {

    private final Map<ResourceLocation, Integer> recipeUseCounts = Maps.newHashMap();
    private ITextComponent customName;
    private EnergyStorageExt energyStorage = new EnergyStorageExt(4000, 128, 128);
    public int progress;
    public int progressMax;
    private final Map<ResourceLocation, Integer> field_214022_n = Maps.newHashMap();

    protected final IIntArray intArray = new IIntArray() {
        @Override
        public int get(int i) {
            switch (i) {
                case 0:
                    return progress;
                case 1:
                    return progressMax;
                case 2:
                    return energyStorage.getEnergyStored();
                case 3:
                    return energyStorage.getMaxEnergyStored();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int i, int value) {
            switch (i) {
                case 0:
                    progress = value;
                case 1:
                    progressMax = value;
                case 2:
                    energyStorage.setEnergyStored(value);
                case 3:
                    energyStorage.setMaxEnergyStored(value);
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    private ItemStackHandler energySlot = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
        }

        @Override
        protected void onContentsChanged(int slot) {
            HydraulicPressTileEntity.this.markDirty();
        }
    };
    private ItemStackHandler inputSlot = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            HydraulicPressTileEntity.this.markDirty();
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 0 ? 1 : super.getSlotLimit(slot);
        }
    };
    private ItemStackHandler outputSlots = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            HydraulicPressTileEntity.this.markDirty();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };
    private CombinedInvWrapper combinedHandler = new CombinedInvWrapper(energySlot, inputSlot, outputSlots);
    public RecipeWrapper recipeWrapper = new RecipeWrapper(this.inputSlot);
    private LazyOptional<IItemHandlerModifiable> combinedInvHandler = LazyOptional.of(() -> combinedHandler);
    private LazyOptional<IItemHandlerModifiable> inputSlotHandler = LazyOptional.of(() -> inputSlot);
    private LazyOptional<IItemHandlerModifiable> outputSlotHandler = LazyOptional.of(() -> outputSlots);
    private LazyOptional<IItemHandlerModifiable> energySlotHandler = LazyOptional.of(() -> energySlot);
    private LazyOptional<EnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    public HydraulicPressTileEntity() {
        super(ThreeCoreBase.HYDRAULIC_PRESS_TILE_ENTITY);
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);

        this.progress = nbt.getInt("Progres");
        this.progressMax = nbt.getInt("ProgressMax");
        this.energyStorage = new EnergyStorageExt(4000, 128, 128, nbt.getInt("Energy"));

        if (nbt.contains("EnergySlots"))
            energySlot.deserializeNBT((CompoundNBT) nbt.get("EnergySlots"));
        if (nbt.contains("InputSlots"))
            inputSlot.deserializeNBT((CompoundNBT) nbt.get("InputSlots"));
        if (nbt.contains("OutputSlots"))
            outputSlots.deserializeNBT((CompoundNBT) nbt.get("OutputSlots"));

        int i = nbt.getShort("RecipesUsedSize");
        for (int j = 0; j < i; ++j) {
            ResourceLocation resourcelocation = new ResourceLocation(nbt.getString("RecipeLocation" + j));
            int k = nbt.getInt("RecipeAmount" + j);
            this.recipeUseCounts.put(resourcelocation, k);
        }

        if (nbt.contains("CustomName", 8))
            this.customName = ITextComponent.Serializer.fromJson(nbt.getString("CustomName"));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);

        nbt.putInt("Progress", this.progress);
        nbt.putInt("ProgressMax", this.progressMax);
        nbt.putInt("Energy", this.energyStorage.getEnergyStored());
        nbt.put("EnergySlots", energySlot.serializeNBT());
        nbt.put("InputSlots", inputSlot.serializeNBT());
        nbt.put("OutputSlots", outputSlots.serializeNBT());

        nbt.putShort("RecipesUsedSize", (short) this.recipeUseCounts.size());
        int i = 0;

        for (Map.Entry<ResourceLocation, Integer> entry : this.recipeUseCounts.entrySet()) {
            nbt.putString("RecipeLocation" + i, entry.getKey().toString());
            nbt.putInt("RecipeAmount" + i, entry.getValue());
            ++i;
        }

        if (this.customName != null)
            nbt.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        return nbt;
    }

    @Override
    public void tick() {
        boolean working = this.isWorking();
        boolean dirty = false;

        if (this.world != null && !this.world.isRemote) {
            this.energySlot.getStackInSlot(0).getCapability(CapabilityEnergy.ENERGY).ifPresent((e) -> {
                int energy = e.extractEnergy(e.getEnergyStored(), true);
                energy = energyStorage.receiveEnergy(energy, true);
                if (energy > 0) {
                    this.energyStorage.receiveEnergy(energy, false);
                    e.extractEnergy(energy, false);
                }
            });

            ItemStack input = this.inputSlot.getStackInSlot(1);
            if (!input.isEmpty()) {
                PressingRecipe recipe = this.world.getRecipeManager().getRecipe(PressingRecipe.RECIPE_TYPE, this.recipeWrapper, this.world).orElse(null);
                if (canWork(recipe)) {
                    this.progressMax = recipe.getEnergy();
                    if (progress >= progressMax) {
                        produceOutput(recipe);
                        progress = 0;
                        dirty = true;
                    } else {
                        progress++;
                        this.energyStorage.extractEnergy(1, false);
                    }
                } else {
                    progress = 0;
                }
            } else {
                progress = 0;
            }
        }

        if (working != this.isWorking()) {
            dirty = true;
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(HydraulicPressBlock.LIT, Boolean.valueOf(this.isWorking())), 3);
        }

        if (dirty)
            this.markDirty();
    }

    public boolean isWorking() {
        return this.progress > 0;
    }

    public boolean canWork(PressingRecipe recipe) {
        if (recipe != null && this.energyStorage.extractEnergy(1, true) == 1) {
            ItemStack recipeOutput = recipe.getRecipeOutput();
            if (recipeOutput.isEmpty()) {
                return false;
            } else {
                boolean output;
                ItemStack outputSlot = this.outputSlots.getStackInSlot(0);

                if (outputSlot.isEmpty()) {
                    output = true;
                } else if (!outputSlot.isItemEqual(recipeOutput)) {
                    output = false;
                } else if (outputSlot.getCount() + recipeOutput.getCount() <= this.outputSlots.getSlotLimit(0) && outputSlot.getCount() < outputSlot.getMaxStackSize()) {
                    output = true;
                } else {
                    output = outputSlot.getCount() + recipeOutput.getCount() <= recipeOutput.getMaxStackSize();
                }

                return output;
            }
        } else {
            return false;
        }
    }

    public void produceOutput(PressingRecipe recipe) {
        if (recipe != null && this.canWork(recipe)) {
            ItemStack recipeOutput = recipe.getRecipeOutput();
            ItemStack outputSlot = outputSlots.getStackInSlot(0);
            if (outputSlot.isEmpty()) {
                this.outputSlots.setStackInSlot(0, recipeOutput.copy());
            } else if (outputSlot.getItem() == recipeOutput.getItem()) {
                outputSlot.grow(recipeOutput.getCount());
            }

            if (!this.world.isRemote) {
                this.canUseRecipe(this.world, (ServerPlayerEntity) null, recipe);
            }

            this.inputSlot.getStackInSlot(1).shrink(1);
        }
    }

    public boolean isUsableByPlayer(PlayerEntity player) {
        if (Objects.requireNonNull(this.world).getTileEntity(this.pos) != this) {
            return false;
        } else {
            return !(player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    public ITextComponent getName() {
        return this.customName != null ? this.customName : new TranslationTextComponent("container.threecore.hydraulic_press");
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return this.customName;
    }

    public void setCustomName(@Nullable ITextComponent name) {
        this.customName = name;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.getName();
    }

    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new HydraulicPressContainer(id, playerInventory, this, this.intArray);
    }

    @Override
    public void fillStackedContents(RecipeItemHelper recipeItemHelper) {
        for (int i = 0; i < this.combinedHandler.getSlots(); i++) {
            ItemStack stack = this.combinedHandler.getStackInSlot(i);
            recipeItemHelper.accountStack(stack);
        }
    }

    @Override
    public void setRecipeUsed(@Nullable IRecipe recipe) {
        if (recipe != null) {
            this.field_214022_n.compute(recipe.getId(), (resourceLocation, integer) -> 1 + (integer == null ? 0 : integer));
        }
    }

    @Nullable
    public IRecipe getRecipeUsed() {
        return null;
    }

    public Map<ResourceLocation, Integer> getRecipeUseCounts() {
        return this.recipeUseCounts;
    }

    @Override
    public boolean canUseRecipe(World worldIn, ServerPlayerEntity player, @Nullable IRecipe recipe) {
        if (recipe != null) {
            this.setRecipeUsed(recipe);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onCrafting(PlayerEntity player) {

    }

    public void unlockRecipes(PlayerEntity player) {
        List<IRecipe<?>> list = Lists.newArrayList();
        Iterator var3 = this.field_214022_n.entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry<ResourceLocation, Integer> entry = (Map.Entry) var3.next();
            player.world.getRecipeManager().getRecipe(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                spawnXP(player, entry.getValue(), ((PressingRecipe) recipe).getExperience());
            });
        }

        player.unlockRecipes(list);
        this.field_214022_n.clear();
    }

    private static void spawnXP(PlayerEntity player, int amount, float value) {
        int i;
        if (value == 0.0F) {
            amount = 0;
        } else if (value < 1.0F) {
            i = MathHelper.floor((float) amount * value);
            if (i < MathHelper.ceil((float) amount * value) && Math.random() < (double) ((float) amount * value - (float) i)) {
                ++i;
            }

            amount = i;
        }

        while (amount > 0) {
            i = ExperienceOrbEntity.getXPSplit(amount);
            amount -= i;
            player.world.addEntity(new ExperienceOrbEntity(player.world, player.posX, player.posY + 0.5D, player.posZ + 0.5D, i));
        }

    }

    public int getEnergy() {
        return this.energyStorage.getEnergyStored();
    }

    public int getMaxEnergy() {
        return this.energyStorage.getMaxEnergyStored();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null)
                return combinedInvHandler.cast();
            else if (side == Direction.UP)
                return inputSlotHandler.cast();
            else if (side == Direction.DOWN)
                return outputSlotHandler.cast();
            else
                return energySlotHandler.cast();
        } else if (cap == CapabilityEnergy.ENERGY)
            return energyHandler.cast();
        return super.getCapability(cap, side);
    }
}