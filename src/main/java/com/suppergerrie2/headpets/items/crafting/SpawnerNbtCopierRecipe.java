package com.suppergerrie2.headpets.items.crafting;

import com.suppergerrie2.headpets.entities.EntityHeadPet;
import com.suppergerrie2.headpets.items.ItemSpawnPet;
import com.suppergerrie2.headpets.items.ItemTreat;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class SpawnerNbtCopierRecipe extends ShapelessOreRecipe {

	public SpawnerNbtCopierRecipe(ResourceLocation group, NonNullList<Ingredient > recipe, ItemStack recipeOutput) {
		super(group, recipe, recipeOutput);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack result = this.output.copy();

		for(int x = 0; x <inv.getWidth(); x++) {
			for (int y = 0; y < inv.getHeight(); y++) {
				if(inv.getStackInRowAndColumn(x,y).getItem() instanceof ItemSpawnPet) {
					if(inv.getStackInRowAndColumn(x,y).hasTagCompound()) {
						result.setTagCompound(inv.getStackInRowAndColumn(x, y).getTagCompound());
						break;
					}
				}
			}
		}

		return result;
	}


}
