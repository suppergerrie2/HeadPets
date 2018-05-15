package com.suppergerrie2.headpets.items;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.suppergerrie2.headpets.entities.HeadPet;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.management.PlayerProfileCache;
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
			HeadPet pet = new HeadPet(worldIn, player.getGameProfile().getId());
			
			ItemStack itemstack = player.getHeldItem(hand);
			GameProfile profile = null;
			
			if(itemstack.hasTagCompound()) {
				NBTTagCompound nbttagcompound = itemstack.getTagCompound();

                if (nbttagcompound.hasKey("GameProfile", 10)) {
                	profile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("GameProfile"));
                }
			} 
			if(profile==null){
				profile = player.getGameProfile();
			}
//			PlayerProfileCache profileCache = worldIn.getMinecraftServer().getPlayerProfileCache();
//			profile = profileCache.getGameProfileForUsername("Dinnerbone");
			
//			MinecraftSessionService sessionService = worldIn.getMinecraftServer().getMinecraftSessionService();
//			profile = sessionService.fillProfileProperties(profile, false);
			
			String text = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzdlMjI4YjllOTVjOTg2NzIxMTU1NWFjYjE1N2IwYmFhODJiZjhhY2E0MThmY2UwNjFlN2YyZjQyMWNlOGJkZSJ9fX0=";
			if(profile.getProperties().containsKey("textures")) {
				 Property property = (Property)Iterables.getFirst(profile.getProperties().get("textures"), (Object)null);
				 text = property.getValue();
			}
			
			pet.setTexture(text);//			pet.setRenderPlayerName(player.getName());
			
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
