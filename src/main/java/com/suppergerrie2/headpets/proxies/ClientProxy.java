package com.suppergerrie2.headpets.proxies;

import com.suppergerrie2.headpets.EventHandler;
import com.suppergerrie2.headpets.entities.EntityHeadEvil;
import com.suppergerrie2.headpets.entities.EntityHeadPet;
import com.suppergerrie2.headpets.entities.rendering.RenderEntityHead;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityHeadPet.class, new IRenderFactory<EntityHeadPet>() {

			@Override
			public Render<? super EntityHeadPet> createRenderFor(RenderManager manager) {
				return new RenderEntityHead(manager);
			}
		});
		
		RenderingRegistry.registerEntityRenderingHandler(EntityHeadEvil.class, new IRenderFactory<EntityHeadEvil>() {

			@Override
			public Render<? super EntityHeadEvil> createRenderFor(RenderManager manager) {
				return new RenderEntityHead(manager);
			}
		});
		
		MinecraftForge.EVENT_BUS.register(new EventHandler.EventHandlerClient());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}

}
