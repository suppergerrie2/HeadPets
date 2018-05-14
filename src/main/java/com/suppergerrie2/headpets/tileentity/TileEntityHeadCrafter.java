package com.suppergerrie2.headpets.tileentity;

import javax.annotation.Nullable;

import com.suppergerrie2.headpets.inventory.ItemHandlerHeadCrafter;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityHeadCrafter extends TileEntity implements ITickable {

	private final ItemHandlerHeadCrafter itemHandler;
	
	public TileEntityHeadCrafter() {
        super();
        itemHandler = new ItemHandlerHeadCrafter(this);
    }
	
	@Override
	public void update() {
		
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        super.readFromNBT(nbtTagCompound);
        itemHandler.deserializeNBT(nbtTagCompound.getCompoundTag("ItemHandler"));
    }
	
	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound)
    {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setTag("ItemHandler", itemHandler.serializeNBT());
        
        return nbtTagCompound;
    }

	public ItemHandlerHeadCrafter getItemHandler()
    {
        return itemHandler;
    }
	
    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) itemHandler;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

	public void startCrafting(String text) {
	}
}