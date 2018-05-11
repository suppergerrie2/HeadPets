package com.suppergerrie2.headpets.proxies;

import com.suppergerrie2.headpets.entities.Pet;
import com.suppergerrie2.headpets.entities.rendering.RenderPet;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(Pet.class, new IRenderFactory<Pet>() {

			@Override
			public Render<? super Pet> createRenderFor(RenderManager manager) {
				return new RenderPet(manager);
			}
		});
	}

	@Override
	public void init(FMLInitializationEvent event) {
		
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}

}
