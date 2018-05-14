package com.suppergerrie2.headpets.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.suppergerrie2.headpets.HeadPets;
import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.inventory.ContainerHeadCrafter;
import com.suppergerrie2.headpets.networking.HeadCraftingStartMessage;
import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiHeadCrafter extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID + ":textures/gui/head_crafter.png");
	private final InventoryPlayer player;
	private final TileEntityHeadCrafter tileentity;
    private GuiButton doneBtn;
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
		doneBtn = this.addButton(new GuiButtonArrow(0, this.width/2-9, 71));
		this.headTextField = new GuiTextField(2, this.fontRenderer, (this.width - 75) / 2, 41, 75, 15);
        this.headTextField.setMaxStringLength(32500);
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
        Keyboard.enableRepeatEvents(false);
    }
	
	protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
		if(!this.headTextField.isFocused()) {
			super.keyTyped(typedChar, keyCode);
		}
		this.headTextField.textboxKeyTyped(typedChar, keyCode);
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
        this.headTextField.drawTextBox();
	}

	protected void actionPerformed(GuiButton button)
    {
		if(button == doneBtn) {
			if(headTextField.getText().length()>0) {
				System.out.println("Start crafting");
				HeadPets.NETWORK_INSTANCE.sendToServer(new HeadCraftingStartMessage(headTextField.getText(), tileentity.getPos()));
			}
		}
    }
}
