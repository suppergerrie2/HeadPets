package com.suppergerrie2.headpets.items.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.items.ItemSpawnPet;
import com.suppergerrie2.headpets.items.ItemTreat;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class SpawnerTreatCombineRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		
		ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context, json);
		
		int treatCount = 0;
		for(Ingredient ingredient : recipe.getIngredients()) {
			for(ItemStack stack : ingredient.getMatchingStacks()) {
				if(stack.getItem() instanceof ItemTreat) {
					treatCount++;
					break;
				}
			}
		}

		boolean hasSpawner = false;
		for(Ingredient ingredient : recipe.getIngredients()) {
			if(hasSpawner) break;
		    for(ItemStack stack : ingredient.getMatchingStacks()) {
				if(stack.getItem() instanceof ItemSpawnPet) {
                    hasSpawner = true;
					break;
				}
			}
		}
		
		if(treatCount!=1) {
			throw new JsonParseException("Treat combine recipe needs 1 treat. Now has " + treatCount + " treats");
		} else if(!hasSpawner) {
		    throw  new JsonParseException("Treat combine recipe needs at least 1 spawner.");
        } else if(!(recipe.getRecipeOutput().getItem() instanceof ItemSpawnPet)) {
            throw  new JsonParseException("Treat combine recipe output needs to be spawner.");
        }
		
		return new SpawnerTreatCombineRecipe(new ResourceLocation(Reference.MODID,recipe.getGroup()), recipe.getIngredients(), recipe.getRecipeOutput());
	}



}
