package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.common.block.ModularFrameTile;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;
import java.util.function.Supplier;

public class Blocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModularItemFrame.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ModularItemFrame.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = Items.ITEMS;


    public static final RegistryObject<Block> MODULAR_FRAME = create(
            "modular_frame", () -> new ModularFrameBlock(ModularFrameBlock.DEFAULT_SETTINGS));

    public static final RegistryObject<TileEntityType<ModularFrameTile>> MODULAR_FRAME_TILE_TYPE =
            TILE_ENTITIES.register("modular_frame",
                    () -> TileEntityType.Builder.create(ModularFrameTile::new, MODULAR_FRAME.get()).build(null));

    private static <T extends Block> RegistryObject<T> create(String name, Supplier<? extends T> sup) {
        return create(name, sup, block -> () -> new BlockItem(block.get(), new Item.Properties().group(ModularItemFrame.TAB)));
    }

    private static <T extends Block> RegistryObject<T> create(String name, Supplier<? extends T> sup, Function<RegistryObject<T>, Supplier<? extends Item>> itemCreator) {
        RegistryObject<T> rb = BLOCKS.register(name, sup);
        ITEMS.register(name, itemCreator.apply(rb));
        return rb;
    }
}
