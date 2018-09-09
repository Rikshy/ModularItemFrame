package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleVacuumTeleporter extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_itemtelein");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t3_itemtelein");

    private static final String NBT_LINK = "item_linked_pos";
    private static final String NBT_LINKX = "linked_posX";
    private static final String NBT_LINKY = "linked_posY";
    private static final String NBT_LINKZ = "linked_posZ";

    public BlockPos linkedLoc = null;

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Nonnull
    @Override
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARDEST_LOC;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.itemtelein");
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
        NBTTagCompound nbt = driver.getTagCompound();
        if (playerIn.isSneaking()) {
            if (nbt == null) nbt = new NBTTagCompound();
            nbt.setLong(NBT_LINK, tile.getPos().toLong());
            driver.setTagCompound(nbt);
            playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.loc_saved"));
        } else {
            if (nbt != null && nbt.hasKey(NBT_LINK)) {
                BlockPos tmp = BlockPos.fromLong(nbt.getLong(NBT_LINK));
                TileEntity targetTile = tile.getWorld().getTileEntity(tmp);
                if (!(targetTile instanceof TileModularFrame) || !((((TileModularFrame) targetTile).module instanceof ModuleDispenserTeleporter)))
                    playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.invalid_target"));
                else if (tile.getPos().getDistance(tmp.getX(), tmp.getY(), tmp.getZ()) > ConfigValues.BaseTeleportRange + (countRange * 10)) {
                    playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.too_far", ConfigValues.BaseTeleportRange + (countRange * 10)));
                } else {
                    linkedLoc = tmp;
                    ((ModuleDispenserTeleporter) ((TileModularFrame) targetTile).module).linkedLoc = tile.getPos();
                    playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.link_established"));
                    nbt.removeTag(NBT_LINK);
                    driver.setTagCompound(nbt);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!hasValidConnection(world)) return;
        if (world.getTotalWorldTime() % (60 - 10 * countSpeed) != 0) return;

        List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, getVacuumBB(pos));
        for (EntityItem entity : entities) {
            ItemStack entityStack = entity.getItem();
            if (entity.isDead || entityStack.isEmpty()) continue;

            ItemUtils.ejectStack(world, linkedLoc, world.getBlockState(pos).getValue(BlockModularFrame.FACING), entityStack);
            entity.setDead();
            world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, entity.posX, entity.posY, entity.posZ, world.rand.nextGaussian(), 0.0D, world.rand.nextGaussian());
            break;
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        if (linkedLoc != null) {
            compound.setInteger(NBT_LINKX, linkedLoc.getX());
            compound.setInteger(NBT_LINKY, linkedLoc.getY());
            compound.setInteger(NBT_LINKZ, linkedLoc.getZ());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_LINKX))
            linkedLoc = new BlockPos(nbt.getInteger(NBT_LINKX), nbt.getInteger(NBT_LINKY), nbt.getInteger(NBT_LINKZ));
    }

    private boolean hasValidConnection(@Nonnull World world) {
        if (linkedLoc == null) return false;
        TileEntity tile = world.getTileEntity(linkedLoc);
        if (!(tile instanceof TileModularFrame) || !(((TileModularFrame) tile).module instanceof ModuleDispenserTeleporter))
            return false;
        return true;
    }

    private AxisAlignedBB getVacuumBB(@Nonnull BlockPos pos) {
        int range = ConfigValues.BaseVacuumRange + countRange;
        switch (tile.blockFacing()) {
            case DOWN:
                return new AxisAlignedBB(pos.add(-5, 0, -5), pos.add(5, 5, 5));
            case UP:
                return new AxisAlignedBB(pos.add(-5, 0, -5), pos.add(5, -5, 5));
            case NORTH:
                return new AxisAlignedBB(pos.add(-5, -5, 0), pos.add(5, 5, 5));
            case SOUTH:
                return new AxisAlignedBB(pos.add(-5, -5, 0), pos.add(5, 5, -5));
            case WEST:
                return new AxisAlignedBB(pos.add(0, -5, -5), pos.add(-5, 5, 5));
            case EAST:
                return new AxisAlignedBB(pos.add(0, -5, -5), pos.add(5, 5, 5));
        }
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }

}
