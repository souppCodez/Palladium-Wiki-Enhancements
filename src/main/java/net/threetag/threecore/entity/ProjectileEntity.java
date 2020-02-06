package net.threetag.threecore.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

@OnlyIn(value = Dist.CLIENT, _interface = IRendersAsItem.class)
public class ProjectileEntity extends ThrowableEntity implements IRendersAsItem, IEntityAdditionalSpawnData {

    public float damage = 3F;
    public float gravityVelocity = 0.03F;
    public boolean dieOnBlockHit = true;
    public boolean dieOnEntityHit = true;
    public boolean particles = true;
    public ProjectileRenderInfo renderInfo = new ProjectileRenderInfo(new ItemStack(Blocks.STONE));

    public ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public ProjectileEntity(World world, double x, double y, double z) {
        super(TCEntityTypes.PROJECTILE.get(), x, y, z, world);
    }

    public ProjectileEntity(World world, LivingEntity livingEntity) {
        super(TCEntityTypes.PROJECTILE.get(), livingEntity, world);
    }

    @Override
    protected void registerData() {

    }

    @Override
    protected float getGravityVelocity() {
        return this.gravityVelocity;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.getType() == RayTraceResult.Type.ENTITY) {
            Entity entity = ((EntityRayTraceResult) result).getEntity();
            entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.damage);

            if (this.dieOnEntityHit && !this.world.isRemote) {
                if (this.particles)
                    this.world.setEntityState(this, (byte) 3);
                this.remove();
            }
        }

        if (result.getType() == RayTraceResult.Type.BLOCK && this.dieOnBlockHit && !this.world.isRemote) {
            if (this.particles)
                this.world.setEntityState(this, (byte) 3);
            this.remove();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private IParticleData makeParticle() {
        ItemStack stack = this.getRenderedItem();
        return stack.isEmpty() ? ParticleTypes.CLOUD : new ItemParticleData(ParticleTypes.ITEM, stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleStatusUpdate(byte state) {
        if (state == 3) {
            IParticleData particle = this.makeParticle();

            for (int i = 0; i < 8; ++i) {
                this.world.addParticle(particle, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putFloat("Damage", this.damage);
        compound.putFloat("GravityVelocity", this.gravityVelocity);
        compound.putBoolean("DieOnEntityHit", this.dieOnEntityHit);
        compound.putBoolean("DieOnBlockHit", this.dieOnBlockHit);
        compound.putBoolean("Particles", this.particles);
        compound.put("RenderInfo", this.renderInfo.serializeNBT());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("Damage", Constants.NBT.TAG_ANY_NUMERIC))
            this.damage = compound.getInt("Damage");
        if (compound.contains("GravityVelocity", Constants.NBT.TAG_ANY_NUMERIC))
            this.gravityVelocity = compound.getInt("gravityVelocity");
        if (compound.contains("DieOnEntityHit"))
            this.dieOnEntityHit = compound.getBoolean("DieOnEntityHit");
        if (compound.contains("DieOnBlockHit"))
            this.dieOnBlockHit = compound.getBoolean("DieOnBlockHit");
        if (compound.contains("Particles"))
            this.particles = compound.getBoolean("Particles");
        this.renderInfo = new ProjectileRenderInfo(compound.getCompound("RenderInfo"));
    }

    public ItemStack getRenderedItem() {
        return this.renderInfo.isItem() ? this.renderInfo.getStack() : ItemStack.EMPTY;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack getItem() {
        return this.getRenderedItem();
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeCompoundTag(this.renderInfo.serializeNBT());
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.renderInfo = new ProjectileRenderInfo(additionalData.readCompoundTag());
    }

    @Nonnull
    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static class ProjectileRenderInfo implements INBTSerializable<CompoundNBT> {

        private ItemStack stack;
        private ResourceLocation modelLayer;
        private boolean energy;

        public ProjectileRenderInfo(ItemStack stack) {
            this.stack = stack;
            this.modelLayer = null;
            this.energy = false;
        }

        public ProjectileRenderInfo(ResourceLocation modelLayer) {
            this.stack = null;
            this.modelLayer = modelLayer;
            this.energy = false;
        }

        public ProjectileRenderInfo(boolean energy) {
            this.stack = null;
            this.modelLayer = null;
            this.energy = true;
        }

        public ProjectileRenderInfo(CompoundNBT nbt) {
            this.deserializeNBT(nbt);
        }

        public boolean isItem() {
            return this.stack != null;
        }

        public ItemStack getStack() {
            return this.stack;
        }

        public ResourceLocation getModelLayer() {
            return this.modelLayer;
        }

        public boolean isEnergy() {
            return this.energy;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            if (this.isItem()) {
                nbt.put("Item", this.stack.serializeNBT());
            } else if (this.getModelLayer() != null) {
                nbt.putString("ModelLayer", this.modelLayer.toString());
            } else {
                nbt.putBoolean("Energy", this.energy);
            }
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            if (nbt.contains("Item")) {
                this.stack = nbt.get("Item") instanceof CompoundNBT ? ItemStack.read(nbt.getCompound("Item")) : new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("Item"))));
            } else if (nbt.contains("ModelLayer")) {
                this.modelLayer = new ResourceLocation(nbt.getString("ModelLayer"));
            } else {
                this.energy = nbt.getBoolean("Energy");
            }
        }
    }
}
