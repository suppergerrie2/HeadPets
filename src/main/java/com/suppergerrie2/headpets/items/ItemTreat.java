package com.suppergerrie2.headpets.items;

import com.suppergerrie2.headpets.ITreatEffects;
import com.suppergerrie2.headpets.entities.EntityHeadPet;
import com.suppergerrie2.headpets.init.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class ItemTreat extends Item {

	public final ItemStack treatDrop;
	public final ITreatEffects effects;
	
	final static ITreatEffects STANDARD = new ITreatEffects() {
		@Override
		public void onDeath(EntityHeadPet head, ItemTreat item, int level, Entity killer) {
			
		}
	};
	
	public ItemTreat(ItemStack treatDrop) {
		this.setRegistryName("treat_"+treatDrop.getUnlocalizedName().replace(".", "_").replace("tile_", "").replace("item_", ""));
		this.setUnlocalizedName("treat_"+treatDrop.getUnlocalizedName().replace(".", "_").replaceAll("tile_", "").replace("item_", ""));
		this.setCreativeTab(ModItems.tabHeadPets);
		this.treatDrop = treatDrop;
		effects = STANDARD;
	}
	
	public ItemTreat(String name, ITreatEffects effects) {
		this.setRegistryName(name);
		this.setUnlocalizedName(name);
		this.setCreativeTab(ModItems.tabHeadPets);
	
		this.treatDrop = ItemStack.EMPTY;
		this.effects = effects;
	}
	
	public String getItemStackDisplayName(ItemStack stack) {
		if(treatDrop.isEmpty()) return super.getItemStackDisplayName(stack);
		return treatDrop.getDisplayName() + " " +  I18n.translateToLocal("item.treat");
	}
	
}
