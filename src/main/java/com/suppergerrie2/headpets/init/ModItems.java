package com.suppergerrie2.headpets.init;

import java.util.ArrayList;
import java.util.List;

import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.items.ItemTreat;
import com.suppergerrie2.headpets.items.SpawnPet;
import com.suppergerrie2.headpets.items.crafting.RecipeHeadPet;

import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStone;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid=Reference.MODID)
public class ModItems {

	public static Item spawnPet;
	public static Item treat;
	private static List<ItemTreat> treats = new ArrayList<ItemTreat>();
	
	private final static ItemStack[] treatTypes = new ItemStack[] {
			new ItemStack(Blocks.LOG2, 1,  0/*BlockPlanks.EnumType.ACACIA.getMetadata()*/),
			new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.ANDESITE.getMetadata()),
			new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata()),
			new ItemStack(Blocks.BLACK_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BLACK.getMetadata()),
			new ItemStack(Blocks.BLUE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BLUE.getMetadata()),
			new ItemStack(Blocks.BROWN_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.BROWN.getMetadata()),
			new ItemStack(Blocks.COAL_BLOCK),
			new ItemStack(Blocks.COBBLESTONE),
			new ItemStack(Blocks.CYAN_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.CYAN.getMetadata()),
			new ItemStack(Blocks.LOG2, 1, 1/*BlockPlanks.EnumType.DARK_OAK.getMetadata()*/),
			new ItemStack(Blocks.DIAMOND_ORE),
			new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.DIORITE.getMetadata()),
			new ItemStack(Blocks.DIRT),
			new ItemStack(Blocks.EMERALD_ORE),
			new ItemStack(Items.EXPERIENCE_BOTTLE),
			new ItemStack(Blocks.GOLD_ORE),
			new ItemStack(Blocks.STONE, 1, BlockStone.EnumType.GRANITE.getMetadata()),
			new ItemStack(Blocks.GRASS),
			new ItemStack(Blocks.GRAY_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.GRAY.getMetadata()),
			new ItemStack(Blocks.GREEN_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.GREEN.getMetadata()),
			new ItemStack(Blocks.IRON_ORE),
			new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()),
			new ItemStack(Blocks.LAPIS_BLOCK),
			new ItemStack(Blocks.LIGHT_BLUE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.LIGHT_BLUE.getMetadata()),
			new ItemStack(Blocks.SILVER_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.SILVER.getMetadata()),
			new ItemStack(Blocks.LIME_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.LIME.getMetadata()),
			new ItemStack(Blocks.MAGENTA_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.MAGENTA.getMetadata()),
			new ItemStack(Blocks.MOSSY_COBBLESTONE),
			new ItemStack(Blocks.MYCELIUM),
			new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata()),
			new ItemStack(Blocks.ORANGE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.ORANGE.getMetadata()),
			new ItemStack(Blocks.PINK_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.PINK.getMetadata()),
			new ItemStack(Blocks.PURPLE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.PURPLE.getMetadata()),
			new ItemStack(Blocks.QUARTZ_ORE),
			new ItemStack(Blocks.RED_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.RED.getMetadata()),
			new ItemStack(Blocks.REDSTONE_ORE),
			new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()),			
			new ItemStack(Blocks.WHITE_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.WHITE.getMetadata()),
			new ItemStack(Blocks.YELLOW_SHULKER_BOX),
			new ItemStack(Blocks.WOOL, 1, EnumDyeColor.YELLOW.getMetadata()),
			
	};
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		spawnPet = new SpawnPet();
		for(ItemStack stack : treatTypes) {
			treats.add(new ItemTreat(stack));
		}
		event.getRegistry().registerAll(spawnPet);
		event.getRegistry().registerAll(treats.toArray(new ItemTreat[0]));
	}
	
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) {
		registerRender(spawnPet);
		for(ItemTreat item : treats) {
			registerRender(item);
		}
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
