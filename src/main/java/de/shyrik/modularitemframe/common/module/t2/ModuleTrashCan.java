package de.shyrik.modularitemframe.common.module.t2;

import com.google.common.collect.ImmutableList;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.PlaySoundPacket;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;

public class ModuleTrashCan extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_trashcan");
    public static final ResourceLocation BG_LOC1 = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t2_trashcan_1");
    public static final ResourceLocation BG_LOC2 = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t2_trashcan_2");
    public static final ResourceLocation BG_LOC3 = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t2_trashcan_3");

    private List<ResourceLocation> frontTex = ImmutableList.of(
            BG_LOC1,
            BG_LOC2,
            BG_LOC3
    );
    private int texIndex = 0;

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation frontTexture() {
        return frontTex.get(texIndex);
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARD_LOC;
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
            if (world.getTotalWorldTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

            EnumFacing facing = tile.blockFacing();
            TileEntity tileTarget = world.getTileEntity(pos.offset(facing));
            if (tileTarget != null) {
                IItemHandlerModifiable trash = (IItemHandlerModifiable) tileTarget.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
                if (trash != null) {
                    for (int slot = 0; slot < trash.getSlots(); slot++) {
                        if (!trash.getStackInSlot(slot).isEmpty()) {
                            trash.setStackInSlot(slot, ItemStack.EMPTY);
                            NetworkHandler.sendAround(new PlaySoundPacket(pos, SoundEvents.BLOCK_LAVA_EXTINGUISH.getSoundName().toString(), SoundCategory.BLOCKS.getName(), 0.4F, 0.7F), pos, world.provider.getDimension());
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
