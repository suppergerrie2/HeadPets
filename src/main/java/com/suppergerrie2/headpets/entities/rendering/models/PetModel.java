package com.suppergerrie2.headpets.entities.rendering.models;

import com.suppergerrie2.headpets.entities.EntityHead;
import com.suppergerrie2.headpets.entities.EntityHeadPet;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelDragonHead;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class PetModel extends ModelBase {
	private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
	private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
	private final ModelDragonHead dragonHead = new ModelDragonHead(0.0F);

	public PetModel() {
	}


	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) { 
		GlStateManager.pushMatrix();
		ModelBase modelbase = this.skeletonHead;

		switch (((EntityHead)entity).getType())
		{
		case ZOMBIE:
			modelbase = this.humanoidHead;
			break;
		case CHAR:
			modelbase = this.humanoidHead;
			break;
		case DRAGON:
			modelbase = this.dragonHead;
		default:
			break;
		}
		
		if(entity instanceof EntityHeadPet) {
			if(((EntityHeadPet) entity).isSitting()) {
				float time = 20;
				float offset = ageInTicks%time;
				offset/=time;
				offset*=2*Math.PI;
				offset = (float) (Math.sin(offset)+1);
				offset*=0.015;
				GlStateManager.scale(1+offset, 1+offset, 1+offset);
				GlStateManager.translate(0, -offset*2, 0);
			}
		}

		GlStateManager.translate(0, 1.5F, 0);
		modelbase.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, 0, scale);

		GlStateManager.popMatrix();

	}

}
