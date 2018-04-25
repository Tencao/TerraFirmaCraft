package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockPitKiln extends Block implements ITileEntityProvider
{
    public static final PropertyBool FULL = PropertyBool.create("full");
    public static final PropertyBool LIT = PropertyBool.create("lit");
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0, 0, 1, 1D/16D, 1);

    public BlockPitKiln()
    {
        super(Material.CIRCUITS);
        setHardness(0.5f);
        setDefaultState(blockState.getBaseState().withProperty(FULL, false).withProperty(LIT, false));
        TileEntity.register(TEPitKiln.ID.toString(), TEPitKiln.class);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te == null) return state;
        return state.withProperty(BlockPitKiln.LIT, te.isLit()).withProperty(BlockPitKiln.FULL, te.hasFuel());
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FULL, LIT);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEPitKiln();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!worldIn.isSideSolid(pos.add(0, -1, 0), EnumFacing.UP))
            worldIn.destroyBlock(pos, true);
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
    {
        return world.getBlockState(pos).getActualState(world, pos).getValue(LIT);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return world.getBlockState(pos).getActualState(world, pos).getValue(LIT) ? 120 : 0; // Twice as much as the highest vanilla level (60)
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos)
    {
        return world.getBlockState(pos).getActualState(world, pos).getValue(LIT);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te == null) return true;
        te.onRightClick(playerIn, playerIn.getHeldItem(hand), hitX < 0.5, hitZ < 0.5);
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te != null) te.onBreakBlock();
        super.breakBlock(worldIn, pos, state); // todo: drop items
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return state.getActualState(world, pos).getValue(FULL);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isTopSolid(IBlockState state)
    {
        // This is required for the fire code, because forge doesn't 'fix' it to use the location sensitive version.
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        // todo: depend on fill level?
        return state.getActualState(source, pos).getValue(FULL) ? FULL_BLOCK_AABB : AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }
}