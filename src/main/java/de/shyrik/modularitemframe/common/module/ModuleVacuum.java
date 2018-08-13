package de.shyrik.modularitemframe.common.module;

import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ModuleVacuum extends ModuleFrameBase {

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return null;
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.vacuum");
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn instanceof FakePlayer && !ConfigValues.AllowFakePlayers) return false;

        ItemStack held = playerIn.getHeldItem(hand);
        if (!playerIn.isSneaking() && !held.isEmpty()) {
            IItemHandlerModifiable handler = getNeighborTileItemCap();
            if (handler != null) {
                ItemStack remain = ItemUtils.giveStack(handler, held);
                held.setCount(remain.getCount());
            }
        }
        return false;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (world.getTotalWorldTime() % ConfigValues.VacuumCooldown != 0) return;

        IItemHandlerModifiable handler = getNeighborTileItemCap();
        if (handler != null) {
            List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)));
            for (EntityItem entity : entities) {
                ItemStack entityStack = entity.getItem();
                if (entity.isDead || entityStack.isEmpty() || ItemUtils.getFittingSlot(handler, entityStack) < 0)
                    continue;

                ItemStack remain = ItemUtils.giveStack(handler, entityStack);
                if (remain.isEmpty())
                    entity.setDead();
                else
                    entity.setItem(remain);
                world.spawnParticle(EnumParticleTypes.LAVA, entity.posX, entity.posY, entity.posZ, world.rand.nextGaussian(), 0.0D, world.rand.nextGaussian());
                break;
            }
        }
    }

    @Nullable
    private IItemHandlerModifiable getNeighborTileItemCap() {
        EnumFacing facing = tile.blockFacing();
        TileEntity te = tile.getNeighbor(facing);

        if (te != null)
            return (IItemHandlerModifiable)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
        return null;
    }
}
