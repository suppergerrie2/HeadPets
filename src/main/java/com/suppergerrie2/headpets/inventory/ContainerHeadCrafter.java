package com.suppergerrie2.headpets.inventory;

import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerHeadCrafter extends Container {

	TileEntityHeadCrafter tileentity;
	
	public ContainerHeadCrafter(InventoryPlayer player, TileEntityHeadCrafter tileentity) {
		this.tileentity = tileentity;
		this.addSlotToContainer(new SlotHeadCrafterInput(new ItemStack(Items.ROTTEN_FLESH), tileentity.getItemHandler(), 0, 34, 24));
		this.addSlotToContainer(new SlotHeadCrafterInput(new ItemStack(Items.GHAST_TEAR), tileentity.getItemHandler(), 1, 56, 24));
		this.addSlotToContainer(new SlotHeadCrafterInput(new ItemStack(Items.DYE, 1, 15), tileentity.getItemHandler(), 2, 34, 45));
		this.addSlotToContainer(new SlotHeadCrafterInput(new ItemStack(Items.MILK_BUCKET), tileentity.getItemHandler(), 3, 56, 45));

		
		this.addSlotToContainer(new SlotHeadCrafterOutput(tileentity.getItemHandler(), 4, 116, 35));
		
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlotToContainer(new Slot(player, x + y*9 + 9, 8 + x*18, 84 + y*18));
			}
		}
		
		for(int x = 0; x < 9; x++)
		{
			this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 142));
		}
	}
	
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
	
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
		ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.inventorySlots.size())
            {
                if (!this.mergeItemStack(itemstack1, 2, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.inventorySlots.size(), false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

}
