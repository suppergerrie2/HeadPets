package com.suppergerrie2.headpets.tileentity;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.suppergerrie2.headpets.inventory.ItemHandlerHeadCrafter;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class TileEntityHeadCrafter extends TileEntity implements ITickable {

	private final ItemHandlerHeadCrafter itemHandler;

	String textureString = "";

	int currentCraftTime;
	int totalCraftTime = 20*5;

	boolean mode = true;
	boolean started = false;

	public TileEntityHeadCrafter() {
		super();
		itemHandler = new ItemHandlerHeadCrafter(this);
	}

	@Override
	public void update() {
		boolean isChanged = false;

		boolean shouldCraft = this.shouldCraft(false);

		if(!this.world.isRemote) {

			if(shouldCraft) {
				currentCraftTime++;
				isChanged = true;

			} else if(currentCraftTime>0){
				currentCraftTime = 0;
				isChanged = true;

			} else {
				this.started = false;
			}

			if(currentCraftTime>=totalCraftTime) {
				try {
					if(this.craftSkull()) {
						currentCraftTime = 0;
						isChanged = true;
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			if(isChanged) {
				this.updateTile();
			}
		} else if(this.currentCraftTime>0) {
			double xOffset = 0.5+(this.world.rand.nextDouble()-0.5);
			double zOffset = 0.5+(this.world.rand.nextDouble()-0.5);

			double xVel = this.world.rand.nextDouble()*0.1-0.05;
			double yVel = this.world.rand.nextDouble()*0.1;
			double zVel = this.world.rand.nextDouble()*0.1-0.05;

			IBlockState redstoneState = Blocks.REDSTONE_BLOCK.getDefaultState();

			this.world.spawnParticle(EnumParticleTypes.BLOCK_DUST, this.pos.getX()+xOffset, this.pos.getY()+0.5, this.pos.getZ()+zOffset, xVel, yVel, zVel, Block.getStateId(redstoneState));
		}
	}

	public boolean shouldCraft(boolean ignoreStarted) {
		if(!started&&!ignoreStarted) return false;

		ItemStack[] recipe = new ItemStack[] {new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.DYE, 1, 15), new ItemStack(Items.MILK_BUCKET)};

		for(int slot = 0; slot < recipe.length; slot++) {
			if(!itemHandler.getStackInSlot(slot).isItemEqual(recipe[slot])) {
				return false;
			}
		}

		return textureString.length()>0;
	}

	private boolean craftSkull() throws UnsupportedEncodingException {

		GameProfile gameprofile;

		if(!mode) {
			gameprofile = new GameProfile(UUID.nameUUIDFromBytes(textureString.getBytes("UTF-8")), null);
			gameprofile.getProperties().put("textures", new Property("textures", this.textureString));
		} else {
			PlayerProfileCache profileCache = this.world.getMinecraftServer().getPlayerProfileCache();
			gameprofile = profileCache.getGameProfileForUsername(this.textureString);

			MinecraftSessionService sessionService = this.world.getMinecraftServer().getMinecraftSessionService();
			gameprofile = sessionService.fillProfileProperties(gameprofile, false);
		}
		ItemStack skull = new ItemStack(Items.SKULL, 1, 3);

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));

		skull.setTagCompound(nbt);

		ItemStack rest = itemHandler.insertItem(4, skull, false);

		if(rest.isEmpty()) {
			for(int slot = 0; slot < itemHandler.getSlots()-1; slot++) {
				itemHandler.extractItem(slot, 1, false);
			}
			itemHandler.setStackInSlot(3, new ItemStack(Items.BUCKET, 1));
		} else {
			return false;
		}

		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound)
	{
		super.readFromNBT(nbtTagCompound);
		itemHandler.deserializeNBT(nbtTagCompound.getCompoundTag("ItemHandler"));
		if(nbtTagCompound.hasKey("TextureString")) {
			this.textureString = nbtTagCompound.getString("TextureString");
		}

		if(nbtTagCompound.hasKey("CraftTime")) {
			this.currentCraftTime = nbtTagCompound.getInteger("CraftTime");
		}

		if(nbtTagCompound.hasKey("Mode")) {
			this.mode = nbtTagCompound.getBoolean("Mode");
		}

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound)
	{
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setTag("ItemHandler", itemHandler.serializeNBT());
		nbtTagCompound.setString("TextureString", this.textureString);
		nbtTagCompound.setInteger("CraftTime", this.currentCraftTime);
		nbtTagCompound.setBoolean("Mode", this.mode);

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

	public void saveCraftingInfo(String text, boolean modeRecieved) {
		if(text.equals(textureString)) return;
		textureString = text;
		this.mode = modeRecieved;
		this.updateTile();
	}

	void updateTile(){
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
		world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
		markDirty();
	}

	public String getTextureString() {
		return textureString;
	}

	public int getCurrentCraftTime() {
		return currentCraftTime;
	}

	public int getTotalCraftTime() {
		return this.totalCraftTime;
	}

	public void startCrafting() {
		this.started = true;
	}
}