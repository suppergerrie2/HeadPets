package com.suppergerrie2.headpets;

import org.apache.logging.log4j.Logger;

import com.suppergerrie2.headpets.client.gui.GuiHeadCrafter;
import com.suppergerrie2.headpets.entities.EntityHeadEvil;
import com.suppergerrie2.headpets.entities.EntityHeadPet;
import com.suppergerrie2.headpets.inventory.ContainerHeadCrafter;
import com.suppergerrie2.headpets.networking.HeadCraftingInfoMessage;
import com.suppergerrie2.headpets.networking.MessageHandler;
import com.suppergerrie2.headpets.proxies.IProxy;
import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MODID, name=Reference.MODNAME, version=Reference.VERSION, acceptedMinecraftVersions=Reference.ACCEPTED_MINECRAFT_VERSIONS)
@Mod.EventBusSubscriber(modid=Reference.MODID)
public class HeadPets implements IGuiHandler {
	
	@Instance
	public static HeadPets instance;
	
	@SidedProxy(modId=Reference.MODID,clientSide="com.suppergerrie2.headpets.proxies.ClientProxy", serverSide="com.suppergerrie2.headpets.proxies.ServerProxy")
	public static IProxy proxy;
	
	public static Logger logger;
	
	static int entityID = 0;
	
	public static final SimpleNetworkWrapper NETWORK_INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
//		this.registerEntity(EntityHeadPet.class, "head_pet");
//		this.registerEntity(EntityHeadEvil.class, "head_evil");
		NETWORK_INSTANCE.registerMessage(MessageHandler.class, HeadCraftingInfoMessage.class, 0, Side.SERVER);
		
		MinecraftForge.EVENT_BUS.register(new com.suppergerrie2.headpets.EventHandler());
		
		proxy.preInit(event);
		logger.info("preInit");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		logger.info("init");
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, this);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		logger.info("postInit");
	}
	
	private void registerEntity(Class<? extends Entity> entityClass, String name) {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID, name), entityClass, name, entityID++, this, 80, 1, true);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return new ContainerHeadCrafter(player.inventory, (TileEntityHeadCrafter) world.getTileEntity(new BlockPos(x,y,z)));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == Reference.GUIID) return new GuiHeadCrafter(player.inventory, (TileEntityHeadCrafter)world.getTileEntity(new BlockPos(x,y,z)));
		return null;
	}
	
}
