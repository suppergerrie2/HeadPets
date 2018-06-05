package com.suppergerrie2.headpets;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.suppergerrie2.headpets.entities.EntityHeadEvil;
import com.suppergerrie2.headpets.entities.EntityHeadPet;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class EventHandler {

	EntityEntry headPet;
	EntityEntry headEvil;
	
	@SubscribeEvent
	public void remapEntity(RegistryEvent.MissingMappings<EntityEntry> e) {
		ImmutableList<Mapping<EntityEntry>> mappings = e.getMappings();
		for(Mapping<EntityEntry> mapping : mappings) {
			if(mapping.key.equals(new ResourceLocation(Reference.MODID, "pet"))) {
				System.out.println(mapping.key);
				Set<ResourceLocation> t = mapping.registry.getKeys();
				System.out.println(t);
			}
		}
	}
	
	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		headPet = new EntityEntry(EntityHeadPet.class, "head_pet").setRegistryName("head_pet");
		headEvil = new EntityEntry(EntityHeadEvil.class, "head_evil").setRegistryName("head_evil");
	    event.getRegistry().registerAll(headPet, headEvil);
	}
	
}
