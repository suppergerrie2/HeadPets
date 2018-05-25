package com.suppergerrie2.headpets.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.suppergerrie2.headpets.HeadPets;
import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.inventory.ContainerHeadCrafter;
import com.suppergerrie2.headpets.networking.HeadCraftingInfoMessage;
import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiHeadCrafter extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID + ":textures/gui/head_crafter.png");
	@SuppressWarnings("unused")
	private final InventoryPlayer player;
	private final TileEntityHeadCrafter tileentity;
    private GuiTextField headTextField;

	public GuiHeadCrafter(InventoryPlayer player, TileEntityHeadCrafter tileentity) {
		super(new ContainerHeadCrafter(player, tileentity));
		this.player = player;
		this.tileentity = tileentity;
	}
	
	@Override
	public void initGui()
    {
		super.initGui();
		this.headTextField = new GuiTextField(2, this.fontRenderer, (this.width - 75) / 2, this.guiTop+4, 75, 15);
        this.headTextField.setMaxStringLength(32500);
        this.headTextField.setText(tileentity.getTextureString());
        this.headTextField.setFocused(false);
        Keyboard.enableRepeatEvents(true);
    }
	
	public void updateScreen()
    {
		super.updateScreen();
        this.headTextField.updateCursorCounter();
    }
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	
	public void onGuiClosed()
    {
		HeadPets.NETWORK_INSTANCE.sendToServer(new HeadCraftingInfoMessage(headTextField.getText(), tileentity.getPos(), headTextField.getText().length()<=16));
		Keyboard.enableRepeatEvents(false);
    }
	
	protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
		if(!this.headTextField.isFocused()) {
			super.keyTyped(typedChar, keyCode);
		}
		this.headTextField.textboxKeyTyped(typedChar, keyCode);
		HeadPets.NETWORK_INSTANCE.sendToServer(new HeadCraftingInfoMessage(headTextField.getText(), tileentity.getPos(), headTextField.getText().length()<=16));
    }
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.headTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		
		this.mc.getTextureManager().bindTexture(TEXTURE);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		float percentDone = (float)tileentity.getCurrentCraftTime()/tileentity.getTotalCraftTime();
		this.drawTexturedModalRect(this.guiLeft+79, this.guiTop+34, 176, 0, (int) (24*percentDone), 17);
        this.headTextField.drawTextBox();
	}
}
