package de.shyrik.modularitemframe.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import de.shyrik.modularitemframe.api.ModuleFrameBase;
import de.shyrik.modularitemframe.common.module.ModuleItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ItemModule extends ItemMod {

	private Class<? extends ModuleFrameBase> moduleClass;

	public ItemModule(@NotNull String name, Class<? extends ModuleFrameBase> moduleClass) {
		super(name);
		this.moduleClass = moduleClass;
	}

	@Nonnull
	public ModuleFrameBase getModule() {
		try {
			return moduleClass.newInstance();
		} catch (Exception x) {
			return new ModuleItem();
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
}
