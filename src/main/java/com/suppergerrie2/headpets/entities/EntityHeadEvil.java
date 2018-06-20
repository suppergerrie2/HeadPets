package com.suppergerrie2.headpets.entities;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityHeadEvil extends EntityHead {

	public EntityHeadEvil(World worldIn) {
		this(worldIn, EnumType.randomType(worldIn.rand));
	}

	public EntityHeadEvil(World worldIn, EnumType type) {
		super(worldIn, type);
	}

	public EntityHeadEvil(World worldIn, EntityHeadPet pet) {
		super(worldIn, pet.getType());

		this.setPosition(pet.posX, pet.posY, pet.posZ);
		
		if(pet.getType()==EnumType.CHAR) {
			GameProfile profile = pet.getTextureProfile();

			if(profile!=null) {
				String text = "";
				if(profile.getProperties().containsKey("textures")) {
					Property property = (Property)Iterables.getFirst(profile.getProperties().get("textures"), (Object)null);
					text = property.getValue();
				}

				this.setTexture(text, profile.getName());
			}
		}
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));
		this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, true));
		this.tasks.addTask(4, new EntityAIWanderAvoidWater(this, 1.0D));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, false));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);

		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);

		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag) {
			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	public void spawnRunningParticles()
	{
		super.spawnRunningParticles();

		if(this.rand.nextInt(16)==0) {
			this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST,
					this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width,
					this.getEntityBoundingBox().maxY + 0.1D,
					this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.width,
					(this.rand.nextFloat()-0.5f)*0.25f,
					0.15D,
					(this.rand.nextFloat()-0.5f)*0.25f,
					Block.getStateId(Blocks.REDSTONE_BLOCK.getDefaultState()));
		}
	}

	@Override
	protected boolean canDespawn() {
		return true;
	}
}