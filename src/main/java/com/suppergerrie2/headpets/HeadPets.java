package com.suppergerrie2.headpets;

import org.apache.logging.log4j.Logger;

import com.suppergerrie2.headpets.entities.Pet;
import com.suppergerrie2.headpets.proxies.IProxy;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = Reference.MODID, name=Reference.MODNAME, version=Reference.VERSION, acceptedMinecraftVersions=Reference.ACCEPTED_MINECRAFT_VERSIONS)
public class HeadPets {
	
	@Instance
	public static HeadPets instance;
	
	@SidedProxy(modId=Reference.MODID,clientSide="com.suppergerrie2.headpets.proxies.ClientProxy", serverSide="com.suppergerrie2.headpets.proxies.ServerProxy")
	public static IProxy proxy;
	
	public static Logger logger;
	
	static int entityID = 0;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		this.registerEntity(Pet.class, "pet");
		
		proxy.preInit(event);
		logger.info("preInit");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		logger.info("init");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		logger.info("postInit");
	}
	
	private void registerEntity(Class<? extends Entity> entityClass, String name) {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID, name), entityClass, name, entityID++, this, 80, 1, true);
	}
	
	
	
}
