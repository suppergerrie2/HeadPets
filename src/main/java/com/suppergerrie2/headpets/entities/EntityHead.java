package com.suppergerrie2.headpets.entities;

import java.util.Random;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public abstract class EntityHead extends EntityTameable implements IEntityAdditionalSpawnData {

	protected static final DataParameter<String> TEXTURE = EntityDataManager.<String>createKey(EntityHeadPet.class, DataSerializers.STRING);
	protected static final DataParameter<String> GNAME = EntityDataManager.<String>createKey(EntityHeadPet.class, DataSerializers.STRING);

	GameProfile gameprofile;

	protected EnumType type;
	
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

		public boolean isEvil() {
			return this==SKELETON||this==WITHER||this==ZOMBIE||this==CREEPER;
		}

		private EnumType(String name, int meta) {
			this.name = name;
			this.metadata = meta;
		}

		public static EnumType fromMetadata(int metadata) {
			if(metadata<0||metadata>=SKULL_TYPES.length) return EnumType.SKELETON;

			return EnumType.valueOf(SKULL_TYPES[metadata].toUpperCase());
		}

		public static EnumType getRandomEvilType(Random rand) {
			int meta = rand.nextInt(4);
			if(meta==3) meta = 4;
			
			return EnumType.fromMetadata(meta);
		}
	}

}
