package com.suppergerrie2.headpets.entities.rendering.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class PetModel extends ModelBase {

	private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
	
	public PetModel() {
	}


	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) { 
		GlStateManager.pushMatrix();
		
		ModelBase modelbase = this.humanoidHead;
		GlStateManager.translate(0, 1.5F, 0);
        modelbase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		
		GlStateManager.popMatrix();

	}
	
}
