package com.suppergerrie2.headpets.init;

import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.items.SpawnPet;
import com.suppergerrie2.headpets.items.crafting.RecipeHeadPet;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid=Reference.MODID)
public class ModItems {

	public static Item spawnPet;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		spawnPet = new SpawnPet();
		event.getRegistry().registerAll(spawnPet);
	}
	
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) {
		registerRender(spawnPet);
	}
	
	private static void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IRecipe recipeHeadPet = new RecipeHeadPet().setRegistryName("head_pet_recipe");
		event.getRegistry().register(recipeHeadPet);
	}
}
