package com.suppergerrie2.headpets.entities;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
import net.minecraft.world.World;

public class HeadPet extends EntityCreature implements IEntityOwnable {

	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(HeadPet.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<String> TEXTURE = EntityDataManager.<String>createKey(HeadPet.class, DataSerializers.STRING);
	
	GameProfile gameprofile;
	
	public HeadPet(World worldIn) {
		this(worldIn, UUID.fromString("a586b43a-8172-4162-96d9-1f058395cf7b"));
	}

	public HeadPet(World worldIn, UUID owner) {
		super(worldIn);
		this.setSize(0.5f, 0.5f);
		this.setOwnerId(owner);
	}

	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
		this.dataManager.register(TEXTURE, "");
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
		
		compound.setString("Texture", dataManager.get(TEXTURE));
		
		if(this.gameprofile!=null) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTUtil.writeGameProfile(nbttagcompound, this.gameprofile);
            compound.setTag("GameProfile", nbttagcompound);
		}
		
	}

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
		 
		 if(compound.hasKey("Texture")) {
			 this.dataManager.set(TEXTURE, compound.getString("Texture"));
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

	@Override
	public EntityLivingBase getOwner() {
		try {
			UUID uuid = this.getOwnerId();
			return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
		} catch (IllegalArgumentException var2) {
			return null;
		}
	}
	
	public void setTexture(String texture) {
		this.dataManager.set(TEXTURE, texture);
		this.gameprofile = new GameProfile(this.getUniqueID(), this.getName());
		this.gameprofile.getProperties().put("textures", new Property("textures", texture));
	}

	public GameProfile getOwnerProfile() {
		if(this.gameprofile==null) {
			String text = dataManager.get(TEXTURE);
			if(text.length()>0) {
				this.gameprofile = new GameProfile(this.getUniqueID(), this.getName());
				this.gameprofile.getProperties().put("textures", new Property("textures", this.dataManager.get(TEXTURE)));
			}
		}
		
		return this.gameprofile;
	}

}
