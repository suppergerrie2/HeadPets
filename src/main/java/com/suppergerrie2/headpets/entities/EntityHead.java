package com.suppergerrie2.headpets.entities;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.suppergerrie2.headpets.HeadPets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.Random;

public abstract class EntityHead extends EntityTameable implements IEntityAdditionalSpawnData {

	protected static final DataParameter<String> TEXTURE = EntityDataManager.<String>createKey(EntityHeadPet.class, DataSerializers.STRING);
	protected static final DataParameter<String> GNAME = EntityDataManager.<String>createKey(EntityHeadPet.class, DataSerializers.STRING);

	GameProfile gameprofile;

	protected EnumType type;

	@SuppressWarnings("unused")
	public EntityHead(World worldIn) {
		this(worldIn, EnumType.SKELETON);
	}

	public EntityHead(World worldIn, EnumType type) {
		super(worldIn);
		this.setSize(0.5f, 0.5f);
		this.setType(type);
	}
	
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(TEXTURE, "");
		this.dataManager.register(GNAME, "");
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, type.name);
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		this.setType(EnumType.valueOf(ByteBufUtils.readUTF8String(buf).toUpperCase()));
	}
	
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		compound.setString("Type", getType().getName());

		if(getType()==EnumType.CHAR) {
			compound.setString("Texture", dataManager.get(TEXTURE));
		}

		if(this.gameprofile!=null) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTUtil.writeGameProfile(nbttagcompound, this.gameprofile);
			compound.setTag("GameProfile", nbttagcompound);
		}
	}
	
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		
		if(compound.hasKey("Type")) {
			this.setType(EnumType.valueOf(compound.getString("Type").toUpperCase()));
		} else {
			this.setType(EnumType.SKELETON);
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

	public GameProfile getTextureProfile() {
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
	
	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
	{
		if(!this.world.isRemote) {
			ItemStack skull = new ItemStack(Items.SKULL, 1, type.metadata);

			if(type==EnumType.CHAR&&this.getTextureProfile()!=null) {
				NBTTagCompound nbt = new NBTTagCompound();
				
				String name;
				if(this.getTextureProfile().getName()!=null&&this.getTextureProfile().getName().length()<=16) {
					name = this.getTextureProfile().getName();
				} else {
					name = this.getName();
				}
				GameProfile profile = new GameProfile(null, name);

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

        public static EnumType randomType(Random rand) {
        	return EnumType.fromMetadata(rand.nextInt(6));
        }

        @Override
		public String getName() {
			return name;
		}

		private EnumType(String name, int meta) {
			this.name = name;
			this.metadata = meta;
		}

		public static EnumType fromMetadata(int metadata) {
			if(metadata<0||metadata>=SKULL_TYPES.length) return EnumType.SKELETON;

			return EnumType.valueOf(SKULL_TYPES[metadata].toUpperCase());
		}
		
		public static EnumType getTypeForEntity(Entity entity) {
			EnumType type;
			
			if(entity==null) {
				HeadPets.logger.warn("Trying to get head type for null entity!");
				type = null;
			} else if(entity instanceof EntitySkeleton) {
				type = SKELETON;
			} else if(entity instanceof EntityWither || entity instanceof EntityWitherSkeleton) {
				type = WITHER;
			}  else if(entity instanceof EntityZombie) {
				type = ZOMBIE;
			} else if(entity instanceof EntityPlayer) {
				type = CHAR;
			} else if(entity instanceof EntityCreeper) {
				type = CREEPER;
			} else if(entity instanceof EntityDragon) {
				type = DRAGON;
			} else {
				type = null;
			}
			
			return type;
		}
	}

}
