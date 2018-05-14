package com.suppergerrie2.headpets.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotHeadCrafterOutput extends SlotItemHandler {

	public SlotHeadCrafterOutput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return false;
	}

}
