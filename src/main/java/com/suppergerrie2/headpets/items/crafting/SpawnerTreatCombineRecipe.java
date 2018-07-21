package com.suppergerrie2.headpets.items.crafting;

import com.suppergerrie2.headpets.items.ItemSpawnPet;
import com.suppergerrie2.headpets.items.ItemTreat;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class SpawnerTreatCombineRecipe extends ShapelessOreRecipe {

	public SpawnerTreatCombineRecipe(ResourceLocation group, NonNullList<Ingredient> recipe, ItemStack recipeOutput) {
		super(group, recipe, recipeOutput);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack result = this.output.copy();
        NBTTagCompound tag = new NBTTagCompound();

		for(int x = 0; x <inv.getWidth(); x++) {
			for (int y = 0; y < inv.getHeight(); y++) {
				if(inv.getStackInRowAndColumn(x,y).getItem() instanceof ItemSpawnPet) {
					if(inv.getStackInRowAndColumn(x,y).hasTagCompound()) {
						tag = inv.getStackInRowAndColumn(x, y).getTagCompound();
						break;
					}
				}
			}
		}
        
        for(int x = 0; x <inv.getWidth(); x++) {
            for (int y = 0; y < inv.getHeight(); y++) {
                if(inv.getStackInRowAndColumn(x,y).getItem() instanceof ItemTreat) {
                    tag.setString("treat", inv.getStackInRowAndColumn(x,y).getItem().getRegistryName().toString());
                }
            }
        }

        result.setTagCompound(tag);

        return result;
    }

}
