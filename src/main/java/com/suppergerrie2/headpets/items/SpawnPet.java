package com.suppergerrie2.headpets.items;

import com.suppergerrie2.headpets.entities.Pet;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpawnPet extends Item {

	public SpawnPet() {
		this.setRegistryName("spawn_pet");
		this.setUnlocalizedName("spawn_pet");
		this.setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		if(!worldIn.isRemote) {
			Pet pet = new Pet(worldIn, player.getGameProfile().getId());
			pet.setRenderPlayerName(player.getName());
			
			double x = pos.getX()+hitX;
			x+=facing.getFrontOffsetX()*pet.getEntityBoundingBox().maxX;
			
			double y = pos.getY()+hitY;
			y+=facing.getFrontOffsetY()*pet.getEntityBoundingBox().maxY;
			
			double z = pos.getZ()+hitZ;
			z+=facing.getFrontOffsetZ()*pet.getEntityBoundingBox().maxZ;
			
			pet.setPosition(x,y, z);
			worldIn.spawnEntity(pet);
		}

		return EnumActionResult.SUCCESS;
	}



}
