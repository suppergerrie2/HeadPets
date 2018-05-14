package com.suppergerrie2.headpets.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotHeadCrafterInput extends SlotItemHandler {

	final ItemStack filter;
	
	public SlotHeadCrafterInput(ItemStack filter, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
		this.filter = filter;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return stack.isItemEqual(filter)?super.isItemValid(stack):false;
	}
}