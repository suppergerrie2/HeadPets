package com.suppergerrie2.headpets.networking;

import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHandler implements IMessageHandler<HeadCraftingInfoMessage, IMessage> {

	@Override
	public IMessage onMessage(HeadCraftingInfoMessage message, MessageContext ctx) {
		EntityPlayerMP serverPlayer = ctx.getServerHandler().player;

		serverPlayer.getServerWorld().addScheduledTask(() -> {
	      TileEntity t = serverPlayer.getServerWorld().getTileEntity(message.tilePosReceived);
	      if(t instanceof TileEntityHeadCrafter) {
	    	  ((TileEntityHeadCrafter)t).saveCraftingInfo(message.headStringReceived, message.modeRecieved);
	      }
	    });
	    
	    return null;
	}

}
