package com.suppergerrie2.headpets.inventory;

import javax.annotation.Nonnull;

import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerHeadCrafter extends ItemStackHandler {

	private final TileEntityHeadCrafter tile;
	
	public ItemHandlerHeadCrafter(TileEntityHeadCrafter tile) {
		super(5);
		this.tile = tile;
	}
	
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (!isStackValidForSlot(slot, stack))
            return stack;

        return super.insertItem(slot, stack, simulate);
    }

    private boolean isStackValidForSlot(int index, @Nonnull ItemStack stack)
    {
       return true;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        super.onContentsChanged(slot);

        tile.markDirty();
    }
	
}
