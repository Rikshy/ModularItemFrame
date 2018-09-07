package de.shyrik.modularitemframe.api;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemUpgrade extends Item {

    private String upgradeId;

    public ItemUpgrade(@Nonnull String name) {
        super();
        setRegistryName(new ResourceLocation(ModularItemFrame.MOD_ID, name));
        setTranslationKey(name);
        setCreativeTab(ModularItemFrame.TAB);
        upgradeId = name;
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tmp = world.getTileEntity(pos);
        if (tmp instanceof TileModularFrame) {
            TileModularFrame tile = (TileModularFrame) tmp;
            if(!world.isRemote && tile.acceptsUpgrade()) {
                UpgradeBase upgrade = UpgradeRegistry.createModuleInstance(upgradeId);
                if (upgrade != null && tile.countUpgradeOfType(upgrade.getClass()) < upgrade.getMaxCount()) {
                    tile.addUpgrade(upgrade);
                    if (!player.isCreative()) player.getHeldItem(hand).shrink(1);
                    tile.markDirty();
                }
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
}
