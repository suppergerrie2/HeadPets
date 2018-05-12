package com.suppergerrie2.headpets.entities.rendering;

import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.suppergerrie2.headpets.entities.HeadPet;
import com.suppergerrie2.headpets.entities.rendering.models.PetModel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderPet extends RenderLiving<HeadPet> {

	public RenderPet(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new PetModel(), 0.3f);
	}

	@Override
	protected ResourceLocation getEntityTexture(HeadPet entity) {
        ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();

        if (entity.getOwnerProfile() != null)
        {
            Minecraft minecraft = Minecraft.getMinecraft();
            Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(entity.getOwnerProfile());

            if (map.containsKey(Type.SKIN))
            {
                resourcelocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
            }
            else
            {
                UUID uuid = EntityPlayer.getUUID(entity.getOwnerProfile());
                resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
            }
        }

		return resourcelocation;
	}

}
