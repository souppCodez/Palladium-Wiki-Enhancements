package net.threetag.threecore.scripts.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.threetag.threecore.scripts.accessors.ItemStackAccessor;
import net.threetag.threecore.scripts.accessors.Vec3dAccessor;

/**
 * Created by Nictogen on 2020-06-25.
 */
public abstract class PlayerInteractScriptEvent extends LivingScriptEvent
{
	public PlayerInteractEvent event;

	public PlayerInteractScriptEvent(PlayerInteractEvent event)
	{
		super(event.getEntityLiving());
		this.event = event;
	}

	public String getHand(){
		return this.event.getHand().toString();
	}

	public Vec3dAccessor getPos()
	{
		BlockPos pos = this.event.getPos();
		return new Vec3dAccessor(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	public ItemStackAccessor getItemStack(){
		return new ItemStackAccessor(this.event.getItemStack());
	}
}