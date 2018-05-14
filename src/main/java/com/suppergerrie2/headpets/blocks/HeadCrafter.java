package com.suppergerrie2.headpets.blocks;

import com.suppergerrie2.headpets.HeadPets;
import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

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
	
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }
	
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }
    
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
    
    public boolean hasTileEntity(IBlockState state) {
    	return true;
    }

    public TileEntity createTileEntity(World world, IBlockState state) {
    	return new TileEntityHeadCrafter();
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float f1, float f2, float f3)
    {
    	if(!world.isRemote) {
    		player.openGui(HeadPets.instance, Reference.GUIID, world, pos.getX(), pos.getY(), pos.getZ());
    	}
    	
    	return true;
    }
	
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityHeadCrafter)
        {
        	ItemStackHandler itemHandler = (ItemStackHandler)te.getCapability( CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP );
            for( int i=0; i<itemHandler.getSlots(); i++ ){
                if( itemHandler.getStackInSlot( i ) != null ){
                	world.spawnEntity( new EntityItem( world, pos.getX(), pos.getY(), pos.getZ(), itemHandler.getStackInSlot( i ) ) );
                }
            }
        }

        super.breakBlock(world, pos, state);
    }
}
