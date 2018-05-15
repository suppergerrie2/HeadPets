package com.suppergerrie2.headpets.entities;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.suppergerrie2.headpets.HeadPets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class HeadPet extends EntityTameable implements IEntityOwnable {

	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(HeadPet.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<String> TEXTURE = EntityDataManager.<String>createKey(HeadPet.class, DataSerializers.STRING);

	EntityAISit aiSit;
	GameProfile gameprofile;

	boolean sitting = false;

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
		this.aiSit = new EntityAISit(this);
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, this.aiSit);        
		this.tasks.addTask(4, new EntityAILeapAtTarget(this, 0.4F));
		this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(6, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
		this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.0D));
		this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(10, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));

	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);

		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);

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

		compound.setBoolean("Sitting", sitting);

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

		if(compound.hasKey("Sitting")) {
			sitting = compound.getBoolean("Sitting");
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		if(this.hurtResistantTime==0&&this.getHealth()<this.getMaxHealth()&&this.ticksExisted%20==0) {
			this.heal(0.25f);
		}
	}

	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (this.isOwner(player) && !this.world.isRemote && hand == EnumHand.MAIN_HAND)
		{
			sitting = !sitting;
			this.aiSit.setSitting(sitting);
			this.isJumping = false;
			this.navigator.clearPath();
			this.setAttackTarget((EntityLivingBase)null);
		}

		if(!world.isRemote) {
			ItemStack itemstack = player.getHeldItem(hand);
			if (itemstack!=null&&itemstack.getItem() instanceof ItemFood)
			{
				ItemFood itemfood = (ItemFood)itemstack.getItem();

				if (itemfood.isWolfsFavoriteMeat() && this.getHealth()<this.getMaxHealth())
				{
					if (!player.capabilities.isCreativeMode)
					{
						itemstack.shrink(1);
					}

					this.heal((float)itemfood.getHealAmount(itemstack));
					return true;
				}
			}
		}

		return super.processInteract(player, hand);
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

	public boolean isOwner(EntityLivingBase entityIn)
	{
		return entityIn == this.getOwner();
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

	public boolean attackEntityAsMob(Entity entityIn)
	{
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag)
		{
			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn)
	{
		if(entitylivingbaseIn instanceof EntityCreeper) {
			return;
		}

		super.setAttackTarget(entitylivingbaseIn);
	}

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if(sitting) {
			this.aiSit.setSitting(false);
			sitting = false;
		}
		return super.attackEntityFrom(source, amount);
	}

	public boolean isTamed() {
		return true;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

}
