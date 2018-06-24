package com.suppergerrie2.headpets.items.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.items.ItemSpawnPet;
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

public class SpawnerNbtCopierRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		
		ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context, json);

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

		if(!hasSpawner) {
			throw new JsonParseException("SpawnerNbtCopierRecipe needs spawner.");
		}
		
		return new SpawnerNbtCopierRecipe(new ResourceLocation(Reference.MODID,recipe.getGroup()), recipe.getIngredients(), recipe.getRecipeOutput());
	}



}
