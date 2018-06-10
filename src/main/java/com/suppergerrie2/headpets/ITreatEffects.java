package com.suppergerrie2.headpets;

import javax.annotation.Nullable;

import com.suppergerrie2.headpets.entities.EntityHeadPet;
import com.suppergerrie2.headpets.items.ItemTreat;

import net.minecraft.entity.Entity;

public interface ITreatEffects {

	void onDeath(EntityHeadPet head, ItemTreat item, int level, @Nullable Entity killer);
	
}
