package com.suppergerrie2.headpets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.resources.I18n;

public class ItemTreat extends Item {

	public final ItemStack treatDrop;
	
	public ItemTreat(ItemStack treatDrop) {
		this.setRegistryName("treat_"+treatDrop.getUnlocalizedName().replace(".", "_").replace("tile_", "").replace("item_", ""));
		this.setUnlocalizedName("treat_"+treatDrop.getUnlocalizedName().replace(".", "_").replaceAll("tile_", "").replace("item_", ""));
		String name = treatDrop.getItem().getRegistryName().toString();
		name+=","+treatDrop.getMetadata();
		System.out.println(name);
		this.setCreativeTab(CreativeTabs.MISC);
		this.treatDrop = treatDrop;
	}

	public String getItemStackDisplayName(ItemStack stack) {
		return treatDrop.getDisplayName() + " " + I18n.format("item.treat");
	}
	
}
