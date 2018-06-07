package com.suppergerrie2.headpets;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.suppergerrie2.headpets.entities.EntityHead.EnumType;
import com.suppergerrie2.headpets.entities.EntityHeadEvil;
import com.suppergerrie2.headpets.entities.EntityHeadPet;
import com.suppergerrie2.headpets.init.ModBlocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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

	@SubscribeEvent
	public void entityDiedEvent(LivingDeathEvent event) {
		Entity e = event.getEntity();
		if(e.world.isRemote) return;
		EnumType type = EnumType.getTypeForEntity(e);
		if(type!=null&&e.world.rand.nextInt(8)==0) {
			EntityHeadEvil head = new EntityHeadEvil(e.world, type);
			head.setPositionAndRotation(e.posX, e.posY, e.posZ, e.rotationYaw, e.rotationPitch);
			e.world.spawnEntity(head);
		}
	}

	@SubscribeEvent
	public void drawSelectionBox(DrawBlockHighlightEvent e) {
		BlockPos blockpos = e.getTarget().getBlockPos();
		IBlockState iblockstate = e.getPlayer().world.getBlockState(blockpos);

		if(iblockstate.getBlock()==ModBlocks.headCrafter) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);

			EntityPlayer player = e.getPlayer();
			float partialTicks = e.getPartialTicks();
			if (iblockstate.getMaterial() != Material.AIR && player.world.getWorldBorder().contains(blockpos))
			{
				double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
				double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
				double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
				
				AxisAlignedBB bottomPart = new AxisAlignedBB(0,0,0,1,0.5,1);
				AxisAlignedBB topPart = new AxisAlignedBB(.25, 0.5, 0.25, 0.75, 1, 0.75);
				
				drawSelectionBoundingBox(bottomPart.grow(0.0020000000949949026D).offset(blockpos).offset(-d3, -d4, -d5), 0.0F, 0.0F, 0.0F, 0.4F);
				drawSelectionBoundingBox(topPart.grow(0.0020000000949949026D).offset(blockpos).offset(-d3, -d4, -d5), 0.0F, 0.0F, 0.0F, 0.4F);
			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			e.setCanceled(true);
		}
	}

	public static void drawSelectionBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha)
	{
		drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
	}

	public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
		drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
		tessellator.draw();
	}

	public static void drawBoundingBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
	{
		buffer.pos(minX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
		buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(minX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
		buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
		buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
		buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
		buffer.pos(maxX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
	}
}

