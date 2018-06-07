package com.suppergerrie2.headpets.entities.rendering;

import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.suppergerrie2.headpets.entities.EntityHead;
import com.suppergerrie2.headpets.entities.rendering.models.PetModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderEntityHead extends RenderLiving<EntityHead> {

	private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
	private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
	private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
	private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");
	private static final ResourceLocation DRAGON_TEXTURES = new ResourceLocation("textures/entity/enderdragon/dragon.png");

	public RenderEntityHead(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new PetModel(), 0.3f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityHead entity) {
		ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();

		switch (entity.getType())
		{
		case SKELETON:
		default:
			return SKELETON_TEXTURES;
		case WITHER:
			return WITHER_SKELETON_TEXTURES;
		case ZOMBIE:
			return ZOMBIE_TEXTURES;
		case CHAR:
			if (entity.getTextureProfile() != null)
			{
				Minecraft minecraft = Minecraft.getMinecraft();
				Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(entity.getTextureProfile());

				if (map.containsKey(Type.SKIN))
				{
					resourcelocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
				}
				else
				{
					UUID uuid = EntityPlayer.getUUID(entity.getTextureProfile());
					resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
				}
			}

			return resourcelocation;
		case CREEPER:
			return CREEPER_TEXTURES;
		case DRAGON:
			return DRAGON_TEXTURES;
		}
	}

	@Override
	protected void applyRotations(EntityHead entityLiving, float p_77043_2_, float rotationYaw, float partialTicks)
	{
		super.applyRotations(entityLiving, p_77043_2_, rotationYaw, partialTicks);
		if(entityLiving.getTextureProfile()!=null&&entityLiving.getTextureProfile().getName()!=null) {
			if(entityLiving.getTextureProfile().getName().equalsIgnoreCase("Dinnerbone"))	{		
				GlStateManager.translate(0.0F, entityLiving.height + 0.1F, 0.0F);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
			}
		}
	}
}
