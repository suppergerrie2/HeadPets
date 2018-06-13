package com.suppergerrie2.headpets.items.crafting;

import com.suppergerrie2.headpets.entities.EntityHeadPet;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class SkullProfileCopierRecipe extends ShapedOreRecipe {

	public SkullProfileCopierRecipe(ResourceLocation group, ItemStack recipeOutput, ShapedPrimer primer) {
		super(group, recipeOutput, primer);
	}

//	@Override
//	public boolean matches(InventoryCrafting inv, World worldIn) {
//		if(inv.getStackInRowAndColumn(0, 0).getItem()==Items.NETHER_STAR&&inv.getStackInRowAndColumn(2, 0).getItem()==Items.NETHER_STAR&&inv.getStackInRowAndColumn(0, 2).getItem()==Items.NETHER_STAR&&inv.getStackInRowAndColumn(2, 2).getItem()==Items.NETHER_STAR) {
//			if(inv.getStackInRowAndColumn(1, 0).getItem()==Items.GHAST_TEAR&&inv.getStackInRowAndColumn(2, 1).getItem()==Items.ROTTEN_FLESH&&inv.getStackInRowAndColumn(1, 2).getItem()==Items.END_CRYSTAL&&inv.getStackInRowAndColumn(0, 1).getItem()==Items.MILK_BUCKET) {
//				if(inv.getStackInRowAndColumn(1, 1).getItem()==Items.SKULL) {
//					return true;
//				}
//			}
//		}
//		
//		return false;
//	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack result = this.output.copy();
		NBTTagCompound tag = new NBTTagCompound();
		EntityHeadPet.EnumType type = EntityHeadPet.EnumType.fromMetadata(inv.getStackInRowAndColumn(1, 1).getMetadata());
		tag.setString("Type", type.getName());
		if(type==EntityHeadPet.EnumType.CHAR) {
			if(inv.getStackInRowAndColumn(1, 1).getSubCompound("SkullOwner")!=null) {
				tag.setTag("GameProfile", inv.getStackInRowAndColumn(1, 1).getSubCompound("SkullOwner"));
			}
		}
		result.setTagCompound(tag);
		
		return result;
	}

//	@Override
//	public boolean canFit(int width, int height) {
//		return width>=3&&height>=3;
//	}
//
//	@Override
//	public ItemStack getRecipeOutput() {
//		return new ItemStack(ModItems.spawnPet);
//	}

}
