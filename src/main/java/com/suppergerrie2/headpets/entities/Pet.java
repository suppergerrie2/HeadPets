package com.suppergerrie2.headpets.entities;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;
import com.suppergerrie2.headpets.HeadPets;
import com.suppergerrie2.headpets.entities.ai.EntityAIFollowOwner;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.world.World;

public class Pet extends EntityCreature implements IEntityOwnable {

	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(Pet.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<String> RENDER_PLAYER_NAME = EntityDataManager.<String>createKey(Pet.class, DataSerializers.STRING);
	
	GameProfile gameprofile;
	
	String texture = null;

	public Pet(World worldIn) {
		this(worldIn, UUID.fromString("a586b43a-8172-4162-96d9-1f058395cf7b"));
	}

	public Pet(World worldIn, UUID owner) {
		super(worldIn);
		this.setSize(0.4f, 0.4f);
		this.setOwnerId(owner);
	}

	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
		this.dataManager.register(RENDER_PLAYER_NAME, "");
	}

	protected void initEntityAI() {
		//		this.aiSit = new EntityAISit(this);
		this.tasks.addTask(1, new EntityAISwimming(this));
		//		this.tasks.addTask(2, this.aiSit);
		this.tasks.addTask(6, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(10, new EntityAILookIdle(this));
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);

		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);

		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}

	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		if (this.getOwnerId() == null)
		{
			compound.setString("OwnerUUID", "");
		}
		else
		{
			compound.setString("OwnerUUID", this.getOwnerId().toString());
		}
		
		if(texture!=null) {
			compound.setString("Texture", texture);
		}
		
		if(this.gameprofile!=null) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTUtil.writeGameProfile(nbttagcompound, this.gameprofile);
            compound.setTag("GameProfile", nbttagcompound);
		}
		
		compound.setString("RenderName", this.getRenderPlayerName());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		String s;

		if (compound.hasKey("OwnerUUID", 8))
		{
			s = compound.getString("OwnerUUID");
		}
		else
		{
			String s1 = compound.getString("Owner");
			s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
		}

		if (!s.isEmpty())
		{
			try
			{
				this.setOwnerId(UUID.fromString(s));
			}
			catch (Throwable var4)
			{
				HeadPets.logger.warn(var4.toString());
			}
		}
		
		 if (compound.hasKey("GameProfile", 10))
         {
             this.gameprofile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag("GameProfile"));
         }
		
		if(compound.hasKey("RenderName")) {
			this.setRenderPlayerName(compound.getString("RenderName"));
		}
	}

	@Override
	public UUID getOwnerId()
	{
		return (UUID)((Optional<?>)this.dataManager.get(OWNER_UNIQUE_ID)).orNull();
	}

	public void setOwnerId(@Nullable UUID p_184754_1_)
	{
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
	}

	public String getRenderPlayerName()
	{
		return this.dataManager.get(RENDER_PLAYER_NAME);
	}

	public void setRenderPlayerName(String name)
	{
		this.dataManager.set(RENDER_PLAYER_NAME, name);
		
		if(gameprofile==null||!gameprofile.getName().equals(name)) {
			if(!this.world.isRemote) {
				gameprofile = new GameProfile(null, name);
			} else {
				gameprofile = null;
			}
			System.out.println(name + "|" + gameprofile.toString());
			gameprofile = TileEntitySkull.updateGameprofile(gameprofile);
		}
	}

	@Override
	public EntityLivingBase getOwner() {
		try {
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}

	public GameProfile getOwnerProfile() {
		if(this.gameprofile==null&&this.getRenderPlayerName().length()>0) {
			gameprofile = TileEntitySkull.updateGameprofile(new GameProfile(null, this.getRenderPlayerName()));
		}
		return this.gameprofile;
	}

}
