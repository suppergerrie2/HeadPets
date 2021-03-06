package com.suppergerrie2.headpets.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.suppergerrie2.headpets.HeadPets;
import com.suppergerrie2.headpets.Reference;
import com.suppergerrie2.headpets.init.ModItems;
import com.suppergerrie2.headpets.items.ItemCraftWand;
import com.suppergerrie2.headpets.tileentity.TileEntityHeadCrafter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class HeadCrafter extends Block {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	protected static final AxisAlignedBB BOTTOM_PART = new AxisAlignedBB(0,0,0,1,0.5,1);
	protected static final AxisAlignedBB TOP_PART = new AxisAlignedBB(.25, 0.5, 0.25, 0.75, 1, 0.75);

	public HeadCrafter(Material materialIn) {
		super(materialIn);
		this.setRegistryName("head_crafter");
		this.setUnlocalizedName("head_crafter");
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		this.setCreativeTab(ModItems.tabHeadPets);
		this.setHardness(1.5F).setResistance(10.0F);
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

	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
	{
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOTTOM_PART);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, TOP_PART);
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
			if(player.getHeldItem(hand).getItem() instanceof ItemCraftWand) {
				TileEntity t = world.getTileEntity(pos);
				if(t!=null&&t instanceof TileEntityHeadCrafter) {
					TileEntityHeadCrafter tileentity = (TileEntityHeadCrafter) t;
					if(tileentity.shouldCraft(true)) {						
						tileentity.startCrafting();
					} else {
						player.openGui(HeadPets.instance, Reference.GUIID, world, pos.getX(), pos.getY(), pos.getZ());
					}

				}
			} else {
				player.openGui(HeadPets.instance, Reference.GUIID, world, pos.getX(), pos.getY(), pos.getZ());
			}
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

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing)state.getValue(FACING)).getIndex();
	}

	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}

	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	@Nullable
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
	{
		List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

		list.add(this.rayTrace(pos, start, end, BOTTOM_PART));
		list.add(this.rayTrace(pos, start, end, TOP_PART));

		RayTraceResult raytraceresult1 = null;
		double d1 = 0.0D;

		for (RayTraceResult raytraceresult : list)
		{
			if (raytraceresult != null)
			{
				double d0 = raytraceresult.hitVec.squareDistanceTo(end);

				if (d0 > d1)
				{
					raytraceresult1 = raytraceresult;
					d1 = d0;
				}
			}
		}

		return raytraceresult1;
	}
}
