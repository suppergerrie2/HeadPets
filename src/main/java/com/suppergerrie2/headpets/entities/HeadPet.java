package com.suppergerrie2.headpets.entities;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.suppergerrie2.headpets.HeadPets;
import com.suppergerrie2.headpets.items.ItemTreat;

import io.netty.buffer.ByteBuf;
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
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class HeadPet extends EntityTameable implements IEntityOwnable, IEntityAdditionalSpawnData  {

	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(HeadPet.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<String> TEXTURE = EntityDataManager.<String>createKey(HeadPet.class, DataSerializers.STRING);
	protected static final DataParameter<String> GNAME = EntityDataManager.<String>createKey(HeadPet.class, DataSerializers.STRING);

	EntityAISit aiSit;
	GameProfile gameprofile;

	boolean sitting = false;

	ItemStack activeTreat = ItemStack.EMPTY;
	int treatLevel = 0;

	int timeTillTreat;

	private EnumType type;

	public HeadPet(World worldIn) {
		this(worldIn, null, EnumType.SKELETON);
	}

	public HeadPet(World worldIn, UUID owner, EnumType type) {
		super(worldIn);
		this.setOwnerId(owner);
		this.setSize(0.5f, 0.5f);
		this.timeTillTreat = this.rand.nextInt(6000) + 6000;
		this.setType(type);
		initEntityAI();
	}

	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
		this.dataManager.register(TEXTURE, "");
		this.dataManager.register(GNAME, "");
	}

	public void writeSpawnData(ByteBuf buffer) {
		System.out.println(type.name);
		ByteBufUtils.writeUTF8String(buffer, type.name);
	}

	public void readSpawnData(ByteBuf buf) {
		this.setType(EnumType.valueOf(ByteBufUtils.readUTF8String(buf).toUpperCase()));
	}

	protected void initEntityAI() {
	   tasks.taskEntries.clear();
	   targetTasks.taskEntries.clear();
		if(this.getOwnerId()==null) {
			this.tasks.addTask(1, new EntityAISwimming(this));
			this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
			this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, true));
			this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1.0D));
			this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false));
			this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
		} else {
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

		compound.setString("Type", getType().getName());

		if(getType()==EnumType.CHAR) {
			compound.setString("Texture", dataManager.get(TEXTURE));
		}

		if(this.gameprofile!=null) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTUtil.writeGameProfile(nbttagcompound, this.gameprofile);
			compound.setTag("GameProfile", nbttagcompound);
		}

		compound.setBoolean("Sitting", sitting);

		if(!this.activeTreat.isEmpty()) {
			NBTTagCompound tag = new NBTTagCompound();
			this.activeTreat.writeToNBT(tag);
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

		if(compound.hasKey("Type")) {
			this.setType(EnumType.valueOf(compound.getString("Type").toUpperCase()));
		} else {
			this.setType(EnumType.SKELETON);
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
			if(this.gameprofile.getName()!=null) {
				this.dataManager.set(GNAME, this.gameprofile.getName());
			}
		}

		if(compound.hasKey("Texture")) {
			this.dataManager.set(TEXTURE, compound.getString("Texture"));
		}

		if(this.aiSit!=null&&compound.hasKey("Sitting")) {
			sitting = compound.getBoolean("Sitting");
			this.aiSit.setSitting(sitting);
		}

		if(compound.hasKey("ActiveTreat")) {
			this.activeTreat = new ItemStack((NBTTagCompound) compound.getTag("ActiveTreat"));
			this.treatLevel = compound.getInteger("TreatLevel");
			this.timeTillTreat = compound.getInteger("TreatTime");
		}
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if(this.hurtResistantTime==0&&this.getHealth()<this.getMaxHealth()&&this.ticksExisted%20==0) {
			this.heal(0.25f);
		}

		if(!this.world.isRemote&&!this.activeTreat.isEmpty()&&--timeTillTreat<0&&this.treatLevel>0) {
			timeTillTreat = this.rand.nextInt(6000) + 6000;
			timeTillTreat/=this.treatLevel;
			this.entityDropItem(this.activeTreat, 0.0f);
		}
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

					if(this.activeTreat.isItemEqual(item.treatDrop)) {
						if(this.treatLevel<2 ) {
							this.treatLevel++;
							this.timeTillTreat/=this.treatLevel;
							if (!player.capabilities.isCreativeMode)
							{
								itemstack.shrink(1);
							}
						}
					} else {
						this.activeTreat=item.treatDrop.copy();
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

	public void setTexture(String texture, String gname) {
		this.dataManager.set(TEXTURE, texture);
		this.dataManager.set(GNAME, gname==null?"":gname);
		if(gname!=null&&gname.equalsIgnoreCase("suppergerrie2")) {
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6);

			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);

			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
		}
		this.gameprofile = new GameProfile(this.getUniqueID(), gname);
		this.gameprofile.getProperties().put("textures", new Property("textures", texture));
	}

	public GameProfile getOwnerProfile() {
		if(this.gameprofile==null) {
			String text = dataManager.get(TEXTURE);
			String gname = dataManager.get(GNAME);
			if(gname==null||gname.length()<=0) {
				gname=this.getName();
			}
			if(text.length()>0) {
				this.gameprofile = new GameProfile(this.getUniqueID(), gname);
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


	public EnumType getType() {
		return type;
	}

	public void setType(EnumType type) {
		this.type = type;
	}
	
	@Override
	public void onDeath(DamageSource source) {
		if(type.isEvil(this.world.getDifficulty())&&this.world.rand.nextInt(8)<=2&&!this.world.isRemote&&this.getOwnerId()!=null) {
			Entity toSpawn = new HeadPet(this.world, null, this.type);
			toSpawn.setPosition(this.posX, this.posY, this.posZ);

			this.world.spawnEntity(toSpawn);
		}
		super.onDeath(source);
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
	{
		if(!this.world.isRemote&&(!type.isEvil(this.world.getDifficulty())||this.getOwnerId()==null)) {
			ItemStack skull = new ItemStack(Items.SKULL, 1, type.metadata);

			if(type==EnumType.CHAR&&this.getOwnerProfile()!=null) {
				NBTTagCompound nbt = new NBTTagCompound();
				GameProfile profile = new GameProfile(null, this.getName());
				profile.getProperties().putAll(this.gameprofile.getProperties());
				nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), profile));

				skull.setTagCompound(nbt);
			}

			this.entityDropItem(skull, 0.1f);
		}
	}
	
	public static enum EnumType implements IStringSerializable
	{
		SKELETON("skeleton", 0),
		WITHER("wither", 1),
		ZOMBIE("zombie", 2),
		CHAR("char", 3),
		CREEPER("creeper", 4),
		DRAGON("dragon", 5);

		private final String name;
		public final int metadata;
		private static final String[] SKULL_TYPES = new String[] {"skeleton", "wither", "zombie", "char", "creeper", "dragon"};

		@Override
		public String getName() {
			return name;
		}

		public boolean isEvil(EnumDifficulty diff) {
			return diff==EnumDifficulty.PEACEFUL?false:this==SKELETON||this==WITHER||this==ZOMBIE||this==CREEPER;
		}

		private EnumType(String name, int meta) {
			this.name = name;
			this.metadata = meta;
		}

		public static EnumType fromMetadata(int metadata) {
			if(metadata<0||metadata>=SKULL_TYPES.length) return EnumType.SKELETON;

			return EnumType.valueOf(SKULL_TYPES[metadata].toUpperCase());
		}
	}

}
