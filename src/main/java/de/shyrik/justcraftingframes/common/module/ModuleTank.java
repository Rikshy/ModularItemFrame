package de.shyrik.justcraftingframes.common.module;

import com.teamwizardry.librarianlib.features.saving.NamedDynamic;
import de.shyrik.justcraftingframes.ConfigValues;
import de.shyrik.justcraftingframes.common.tile.TileModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;

@NamedDynamic(resourceLocation = "module_tank")
public class ModuleTank extends ModuleFluid {

    public ModuleTank(TileModularFrame te) {
        super(te);
        tank.setCapacity(ConfigValues.TankFrameCapacity);
    }

    @Override
    public void onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        FluidUtil.interactWithFluidHandler(playerIn, hand, tank);
        tile.markDirty();
        //return FluidUtil.getFluidHandler(stack) != null; TODO
    }
}
