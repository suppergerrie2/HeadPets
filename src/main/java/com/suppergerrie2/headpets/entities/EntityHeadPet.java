package com.suppergerrie2.headpets.entities;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.suppergerrie2.headpets.HeadPets;
import com.suppergerrie2.headpets.items.ItemTreat;

import net.minecraft.entity.Entity;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityHeadPet extends EntityHead implements IEntityOwnable, IEntityAdditionalSpawnData  {

	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityHeadPet.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	EntityAISit aiSit;
	boolean sitting = false;

	//	ItemStack activeTreat = ItemStack.EMPTY;
	ItemTreat activeTreat = null;
	int treatLevel = 0;

	int timeTillTreat;

	boolean becameEvil = false;

	public EntityHeadPet(World worldIn) {
		this(worldIn, null, EnumType.SKELETON);
	}

	public EntityHeadPet(World worldIn, UUID owner, EnumType type) {
		super(worldIn, type);
		this.setOwnerId(owner);
		this.timeTillTreat = this.rand.nextInt(6000) + 6000;
	}

	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
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

		compound.setBoolean("Sitting", sitting);

		if(this.activeTreat!=null){
			NBTTagCompound tag = new NBTTagCompound();
			new ItemStack(this.activeTreat).writeToNBT(tag);
			compound.setTag("ActiveTreat", tag);
			compound.setInteger("TreatLevel", this.treatLevel);
			compound.setInteger("TreatTime", this.timeTillTreat);
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

		if(this.aiSit!=null&&compound.hasKey("Sitting")) {
			sitting = compound.getBoolean("Sitting");
			this.aiSit.setSitting(sitting);
		}

		if(compound.hasKey("ActiveTreat")) {
			Item i = new ItemStack((NBTTagCompound) compound.getTag("ActiveTreat")).getItem();
			if(i instanceof ItemTreat) {
				this.activeTreat = (ItemTreat) i;
				this.treatLevel = compound.getInteger("TreatLevel");
				this.timeTillTreat = compound.getInteger("TreatTime");
			} else {
				HeadPets.logger.warn("Treat in entity is not a valid treat item. Probably updated from an old version.");
			}
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if(this.hurtResistantTime==0&&this.getHealth()<this.getMaxHealth()&&this.ticksExisted%20==0) {
			this.heal(0.25f);
		}

		if(!this.world.isRemote&&this.activeTreat != null&&!this.activeTreat.treatDrop.isEmpty()&&--timeTillTreat<0&&this.treatLevel>0) {
			timeTillTreat = this.rand.nextInt(6000) + 6000;
			timeTillTreat/=this.treatLevel;
			this.entityDropItem(this.activeTreat.treatDrop, 0.0f);
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		if(this.world.getDifficulty()!=EnumDifficulty.PEACEFUL&&this.world.rand.nextInt(8)<=2&&!this.world.isRemote&&this.getOwnerId()!=null) {

			Entity toSpawn = new EntityHeadEvil(this.world, this);
			toSpawn.setPosition(this.posX, this.posY, this.posZ);

			this.world.spawnEntity(toSpawn);

			becameEvil = true;
		}

		if(this.activeTreat!=null) {
			this.activeTreat.effects.onDeath(this, this.activeTreat, this.treatLevel, source.getTrueSource());
		}

		super.onDeath(source);
	}

	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (this.isOwner(player) && !this.world.isRemote && hand == EnumHand.MAIN_HAND && player.getHeldItem(hand).isEmpty())
		{
			sitting = !sitting;
			this.aiSit.setSitting(sitting);
			this.isJumping = false;
			this.navigator.clearPath();
			this.setAttackTarget((EntityLivingBase)null);
		}

		if(!world.isRemote) {
			ItemStack itemstack = player.getHeldItem(hand);
			if(itemstack!=null) {
				if (itemstack.getItem() instanceof ItemFood)
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
				} else if(itemstack.getItem() instanceof ItemTreat) {
					ItemTreat item = (ItemTreat) itemstack.getItem();

					if(this.activeTreat==item) {
						if(this.treatLevel<2 ) {
							this.treatLevel++;
							this.timeTillTreat/=this.treatLevel;
							if (!player.capabilities.isCreativeMode)
							{
								itemstack.shrink(1);
							}
						}
					} else {
						this.activeTreat=item;//itemstack.copy();//item.treatDrop.copy();
						this.treatLevel=1;

						if (!player.capabilities.isCreativeMode)
						{
							itemstack.shrink(1);
						}
					}
				}
			}
		}

		return super.processInteract(player, hand);
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

	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if(sitting) {
			this.aiSit.setSitting(false);
			sitting = false;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
	{
		if(!this.becameEvil) {
			super.dropLoot(wasRecentlyHit, lootingModifier, source);
		}
	}

	public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn)
	{
		if(entitylivingbaseIn instanceof EntityCreeper) {
			return;
		}

		super.setAttackTarget(entitylivingbaseIn);
	}

	public boolean isTamed() {
		return true;
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
}