package com.suppergerrie2.headpets.client.gui;

import com.suppergerrie2.headpets.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonArrow extends GuiButton {

	protected static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(Reference.MODID + ":textures/gui/arrow.png");
	
	public GuiButtonArrow(int buttonId, int x, int y) {
		super(buttonId, x, y, 24, 17, null);
	}
	
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(BUTTON_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            this.drawTexturedModalRect(this.x, this.y, 0, 0 + i * 17, 24, 17);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

}
