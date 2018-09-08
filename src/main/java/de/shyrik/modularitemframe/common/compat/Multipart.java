package de.shyrik.modularitemframe.common.compat;

import de.shyrik.modularitemframe.Registrar;
import de.shyrik.modularitemframe.common.item.ItemModule;
import de.shyrik.modularitemframe.common.item.ItemUpgrade;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import mcmultipart.api.addon.IMCMPAddon;
import mcmultipart.api.addon.MCMPAddon;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartRegistry;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

import java.util.Optional;

@MCMPAddon
public class Multipart implements IMCMPAddon {

    @Override
    public void registerParts(IMultipartRegistry registry) {
        registry.registerPartWrapper(Registrar.FRAME_MODULAR, new PartBlock(Registrar.FRAME_MODULAR));
        registry.registerStackWrapper(Item.getItemFromBlock(Registrar.FRAME_MODULAR), stack -> true, Registrar.FRAME_MODULAR);
    }

    public static Optional<TileEntity> getTile(World world, BlockPos pos, EnumFacing face) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileModularFrame) {
            return Optional.of(te);
        } else {
            if (Loader.isModLoaded("mcmultipart")) {
                return getPartTile(world, pos, face);
            }
        }
        return Optional.empty();
    }

    @net.minecraftforge.fml.common.Optional.Method(modid = "mcmultipart")
    private static Optional<TileEntity> getPartTile(World world, BlockPos pos, EnumFacing face) {
        return MultipartHelper.getPartTile(world, pos, EnumFaceSlot.fromFace(face))
                .map(IMultipartTile::getTileEntity);
    }

    public class PartBlock implements IMultipart {

        private BlockModularFrame block;

        public PartBlock(BlockModularFrame block) {
            this.block = block;
        }

        @Override
        public Block getBlock() {
            return block;
        }

        @Override
        public IPartSlot getSlotForPlacement(World world, BlockPos pos, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer) {
            return EnumFaceSlot.fromFace(facing);
        }

        @Override
        public void onPartClicked(IPartInfo part, EntityPlayer player, RayTraceResult hit) {
            ((TileModularFrame)part.getTile().getTileEntity()).module.onBlockClicked(part.getActualWorld(), part.getPartPos(), player);
        }

        @Override
        public boolean onPartActivated(IPartInfo part, EntityPlayer playerIn, EnumHand hand, RayTraceResult hit) {
            boolean moveHand;
            TileModularFrame tile = (TileModularFrame)part.getTile().getTileEntity();
            EnumFacing facing = ((EnumFaceSlot)part.getSlot()).getFacing();
            ItemStack handItem = playerIn.getHeldItem(hand);
            World worldIn = part.getActualWorld();
            BlockPos pos = hit.getBlockPos();
            float x = (float) (Math.abs(hit.hitVec.x) - Math.floor(Math.abs(hit.hitVec.x)));
            float y = (float) (Math.abs(hit.hitVec.y) - Math.floor(Math.abs(hit.hitVec.y)));
            float z = (float) (Math.abs(hit.hitVec.z) - Math.floor(Math.abs(hit.hitVec.z)));

            if (handItem.getItem() instanceof ItemScrewdriver) {
                if (!worldIn.isRemote) {
                    if ((hit.sideHit.getAxis().isHorizontal() && hit.sideHit.getOpposite() == facing) || hit.sideHit == facing) {
                        if (BlockModularFrame.hitModule(facing, x, y, z)) {
                            if (ItemScrewdriver.getMode(handItem) == ItemScrewdriver.EnumMode.INTERACT) {
                                tile.module.screw(worldIn, pos, playerIn, handItem);
                            } else tile.dropModule(facing, playerIn);
                        } else tile.dropUpgrades(playerIn, facing);
                        tile.markDirty();
                    }
                }
                moveHand = true;
            } else if (handItem.getItem() instanceof ItemModule) {
                if (!worldIn.isRemote && tile.acceptsModule()) {
                    tile.setModule((ItemModule) handItem.getItem());
                    if (!playerIn.isCreative()) playerIn.getHeldItem(hand).shrink(1);
                    tile.markDirty();
                }
                moveHand = true;
            } else if (handItem.getItem() instanceof ItemUpgrade) {
                if (!worldIn.isRemote && tile.acceptsUpgrade()) {
                    if (tile.tryAddUpgrade((ItemUpgrade) handItem.getItem())) {
                        if (!playerIn.isCreative()) playerIn.getHeldItem(hand).shrink(1);
                        tile.markDirty();
                    }
                }
                moveHand = true;
            }
            else
                moveHand = tile.module.onBlockActivated(worldIn, pos, part.getState(), playerIn, hand, hit.sideHit, x, y, z);
            return moveHand;
        }

        @Override
        public IPartSlot getSlotFromWorld(IBlockAccess world, BlockPos pos, IBlockState state) {
            return EnumFaceSlot.fromFace(state.getValue(BlockModularFrame.FACING));
        }

        @Override
        public IMultipartTile convertToMultipartTile(TileEntity tileEntity) {
            return new PartTile(tileEntity);
        }
    }

    public class PartTile implements IMultipartTile, ITickable {

        private TileModularFrame tile;

        public PartTile(TileEntity tile) {
            this.tile = (TileModularFrame)tile;
        }

        @Override
        public TileEntity getTileEntity() {
            return tile;
        }

        @Override
        public void update() {
            tile.update();
        }
    }
}
