package com.suppergerrie2.headpets.init;

import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.blocks.HeadCrafter;
import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber(modid=Reference.MODID)
public class ModBlocks {

	public static Block headCrafter;
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		headCrafter=new HeadCrafter(Material.ROCK);
		event.getRegistry().registerAll(headCrafter);
		GameRegistry.registerTileEntity(TileEntityHeadCrafter.class, "tile_head_crafter");
	}
	
	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(headCrafter).setRegistryName(headCrafter.getRegistryName()).setUnlocalizedName(headCrafter.getUnlocalizedName()).setCreativeTab(CreativeTabs.DECORATIONS));
	}
	
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) {
		registerRender(Item.getItemFromBlock(headCrafter));
	}
	
	public static void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
	}
}
