package com.suppergerrie2.headpets.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class HeadCrafter extends Block {

	public HeadCrafter(Material materialIn) {
		super(materialIn);
		this.setRegistryName("head_crafter");
		this.setUnlocalizedName("head_crafter");
	}
	
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
	
}
