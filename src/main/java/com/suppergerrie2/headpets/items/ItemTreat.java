package com.suppergerrie2.headpets.items;

import com.suppergerrie2.headpets.init.ModItems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class ItemTreat extends Item {

	public final ItemStack treatDrop;
	
	public ItemTreat(ItemStack treatDrop) {
		this.setRegistryName("treat_"+treatDrop.getUnlocalizedName().replace(".", "_").replace("tile_", "").replace("item_", ""));
		this.setUnlocalizedName("treat_"+treatDrop.getUnlocalizedName().replace(".", "_").replaceAll("tile_", "").replace("item_", ""));
//		String name = treatDrop.getItem().getRegistryName().toString();
//		name+=","+treatDrop.getMetadata();
		this.setCreativeTab(ModItems.tabHeadPets);
		this.treatDrop = treatDrop;
	}
	
	public String getItemStackDisplayName(ItemStack stack) {
		return treatDrop.getDisplayName() + " " +  I18n.translateToLocal("item.treat");
	}
	
}
