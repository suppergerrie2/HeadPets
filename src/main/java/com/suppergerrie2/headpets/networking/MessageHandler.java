package com.suppergerrie2.headpets.networking;

import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHandler implements IMessageHandler<HeadCraftingStartMessage, IMessage> {

	@Override
	public IMessage onMessage(HeadCraftingStartMessage message, MessageContext ctx) {
		EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
	    // The value that was sent
	    String amount = message.headStringReceived;
	    // Execute the action on the main server thread by adding it as a scheduled task
	    serverPlayer.getServerWorld().addScheduledTask(() -> {
	      TileEntity t = serverPlayer.getServerWorld().getTileEntity(message.tilePosReceived);
	      if(t instanceof TileEntityHeadCrafter) {
	    	  ((TileEntityHeadCrafter)t).startCrafting(message.headStringReceived);
	      }
	    });
	    
	    return null;
	}

}
