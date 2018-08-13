package de.shyrik.modularitemframe.api;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ItemModule extends ItemMod {

    public String moduleId;

    public ItemModule(@NotNull String name) {
        super(name);
        moduleId = name;
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tmp = world.getTileEntity(pos);
        if (tmp instanceof TileModularFrame) {
            TileModularFrame tile = (TileModularFrame) tmp;
            if (!world.isRemote && tile.acceptsModule()) {
                ItemStack held = player.getHeldItem(hand);
                tile.setModule(ModuleRegistry.createModuleInstance(((ItemModule) held.getItem()).moduleId));
                held.setCount(held.getCount() - 1);
                tile.markDirty();
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
}
