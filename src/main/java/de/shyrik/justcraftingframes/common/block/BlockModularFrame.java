package de.shyrik.justcraftingframes.common.block;

import de.shyrik.justcraftingframes.client.render.FrameRenderer;
import de.shyrik.justcraftingframes.common.tile.TileModularFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockModularFrame extends BlockFrameBase {

    public BlockModularFrame() {
        super("modular_frame");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileModularFrame.class, new FrameRenderer());
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(@NotNull World world, @NotNull IBlockState state) {
        return new TileModularFrame();
    }
}
