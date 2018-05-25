package com.suppergerrie2.headpets.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class HeadCraftingInfoMessage implements IMessage {

	public HeadCraftingInfoMessage(){}

	private String headString;
	private BlockPos tilePos;
	private boolean mode;
	
	String headStringReceived;
	BlockPos tilePosReceived;
	boolean modeRecieved;

	public HeadCraftingInfoMessage(String toSend, BlockPos blockPos, boolean mode) {
		this.headString = toSend;
		this.tilePos = blockPos;
		this.mode = mode;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		headStringReceived = ByteBufUtils.readUTF8String(buf);
		tilePosReceived = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		this.modeRecieved = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, headString);
		buf.writeInt(tilePos.getX());
		buf.writeInt(tilePos.getY());
		buf.writeInt(tilePos.getZ());
		buf.writeBoolean(mode);
	}
}
