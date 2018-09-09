package de.shyrik.modularitemframe.common.module.t2;

import com.google.common.collect.ImmutableList;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleTrashCan extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_trashcan");

    private List<ResourceLocation> frontTex = ImmutableList.of(
            new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/trashcan_bg_1"),
            new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/trashcan_bg_2"),
            new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/trashcan_bg_3")
    );
    private int texIndex = 0;

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return frontTex.get(texIndex);
    }

    @Nonnull
    @Override
    public ResourceLocation innerTexture() {
        return new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/hard_inner");
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.trash_can");
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!world.isRemote) {
            if (world.getTotalWorldTime() % (60 - 10 * countSpeed) != 0) return;

            EnumFacing facing = tile.blockFacing();
            TileEntity tile = world.getTileEntity(pos.offset(facing));
            if (tile != null) {
                IItemHandlerModifiable trash = (IItemHandlerModifiable) tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
                if (trash != null) {
                    for (int slot = 0; slot < trash.getSlots(); slot++) {
                        if (!trash.getStackInSlot(slot).isEmpty()) {
                            trash.setStackInSlot(slot, ItemStack.EMPTY);
                            world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4F, 0.7F);
                            break;
                        }
                    }
                }
            }
        } else {
            if (world.getTotalWorldTime() % 10 == 0) {
                texIndex = texIndex < frontTex.size() - 1 ? texIndex + 1 : 0;
                reloadModel = true;
            }
        }
    }
}
