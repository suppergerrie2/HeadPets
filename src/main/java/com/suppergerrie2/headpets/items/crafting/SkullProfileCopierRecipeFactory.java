package com.suppergerrie2.headpets.items.crafting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.suppergerrie2.headpets.Reference;

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

public class SkullProfileCopierRecipeFactory implements IRecipeFactory {

	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
		
		int skullCount = 0;
		for(Ingredient ingredient : recipe.getIngredients()) {
			for(ItemStack stack : ingredient.getMatchingStacks()) {
				if(stack.getItem() instanceof ItemSkull) {
					skullCount++;
					break;
				}
			}
		}
		
		if(skullCount!=1) {
			throw new JsonParseException("Skull copier recipe needs 1 skull. Now has " + skullCount + " skulls");
		}
		
		ShapedPrimer primer = new ShapedPrimer();
        primer.width = recipe.getRecipeWidth();
        primer.height = recipe.getRecipeHeight();
        primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
        primer.input = recipe.getIngredients();
		
		return new SkullProfileCopierRecipe(new ResourceLocation(Reference.MODID,recipe.getGroup()), recipe.getRecipeOutput(), primer);
	}



}
