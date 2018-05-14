package com.suppergerrie2.headpets.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class HeadCraftingStartMessage implements IMessage {

	public HeadCraftingStartMessage(){}

	private String headString;
	private BlockPos tilePos;
	
	String headStringReceived;
	BlockPos tilePosReceived;

	public HeadCraftingStartMessage(String toSend, BlockPos blockPos) {
		this.headString = toSend;
		this.tilePos = blockPos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		headStringReceived = ByteBufUtils.readUTF8String(buf);
		tilePosReceived = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, headString);
		buf.writeInt(tilePos.getX());
		buf.writeInt(tilePos.getY());
		buf.writeInt(tilePos.getZ());
	}
}
