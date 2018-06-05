package com.suppergerrie2.headpets;

import com.google.common.collect.ImmutableList;
import com.suppergerrie2.headpets.entities.EntityHeadEvil;
import com.suppergerrie2.headpets.entities.EntityHeadPet;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;;

public class EventHandler {

	EntityEntry headPet;
	EntityEntry headEvil;
	
	@SubscribeEvent
	public void remapEntity(RegistryEvent.MissingMappings<EntityEntry> e) {
		ImmutableList<Mapping<EntityEntry>> mappings = e.getMappings();
		for(Mapping<EntityEntry> mapping : mappings) {
			if(mapping.key.equals(new ResourceLocation(Reference.MODID, "pet"))) {
				System.out.println(mapping.key);
				mapping.remap(headPet);
			}
		}
	}
	
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		headPet = EntityEntryBuilder.create().entity(EntityHeadPet.class).name("head_pet").tracker(80, 1, false).id(new ResourceLocation(Reference.MODID, "head_pet"), 0).build();
		headEvil = EntityEntryBuilder.create().entity(EntityHeadEvil.class).name("head_evil").tracker(80, 1, false).id(new ResourceLocation(Reference.MODID, "head_evil"), 1).build();
	    event.getRegistry().registerAll(headPet, headEvil);
	}
	
}
