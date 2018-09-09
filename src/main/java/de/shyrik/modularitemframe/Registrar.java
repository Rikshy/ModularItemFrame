package de.shyrik.modularitemframe;

import com.google.common.collect.ImmutableList;
import de.shyrik.modularitemframe.api.ModuleRegistry;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.ModuleDispenserTeleporter;
import de.shyrik.modularitemframe.common.module.t3.ModuleTeleport;
import de.shyrik.modularitemframe.common.module.t3.ModuleVacuumTeleporter;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber
public class Registrar {

    public static BlockModularFrame FRAME_MODULAR;
    public static ItemScrewdriver SCREWDRIVER;

    public static List<Block> ALL_BLOCKS;
    public static List<Item> ALL_ITEMS;

    //just so i cna collapse it :)
    static {

        ALL_BLOCKS = ImmutableList.of(
                FRAME_MODULAR = new BlockModularFrame()
        );

        ALL_ITEMS = ImmutableList.of(SCREWDRIVER = new ItemScrewdriver(),

                //Canvas
                new Item().setRegistryName(new ResourceLocation(ModularItemFrame.MOD_ID, "canvas")).setTranslationKey(ModularItemFrame.MOD_ID + ":canvas").setCreativeTab(ModularItemFrame.TAB),

                //Tier 1
                ModuleRegistry.registerCreate(ModuleCrafting.LOC, ModuleCrafting.class),
                ModuleRegistry.registerCreate(ModuleIO.LOC, ModuleIO.class),
                ModuleRegistry.registerCreate(ModuleItem.LOC, ModuleItem.class),
                ModuleRegistry.registerCreate(ModuleNullify.LOC, ModuleNullify.class),
                ModuleRegistry.registerCreate(ModuleTank.LOC, ModuleTank.class),

                //Tier 2
                ModuleRegistry.registerCreate(ModuleCraftingPlus.LOC, ModuleCraftingPlus.class),
                ModuleRegistry.registerCreate(ModuleDispense.LOC, ModuleDispense.class),
                ModuleRegistry.registerCreate(ModuleXP.LOC, ModuleXP.class),
                ModuleRegistry.registerCreate(ModuleVacuum.LOC, ModuleVacuum.class),
                ModuleRegistry.registerCreate(ModuleTrashCan.LOC, ModuleTrashCan.class),

                //Tier 3
                //MODULE_AUTOCRAFTING = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_crafting_plus"), ModuleAutoCrafting.class);
                ModuleRegistry.registerCreate(ModuleTeleport.LOC, ModuleTeleport.class),
                ModuleRegistry.registerCreate(ModuleVacuumTeleporter.LOC, ModuleVacuumTeleporter.class),
                ModuleRegistry.registerCreate(ModuleDispenserTeleporter.LOC, ModuleDispenserTeleporter.class)
        );
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(ALL_BLOCKS.toArray(new Block[0]));

        GameRegistry.registerTileEntity(TileModularFrame.class, BlockModularFrame.LOC);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Block block : ALL_BLOCKS)
            event.getRegistry().register(new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
        event.getRegistry().registerAll(ALL_ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        for (Block block : ALL_BLOCKS)
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Objects.requireNonNull(block.getRegistryName()), "inventory"));
        for (Item item : ALL_ITEMS)
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer(TileModularFrame.class, new FrameRenderer());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerTex(TextureStitchEvent.Pre event) {
        List<ResourceLocation> tex = ImmutableList.of(
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/hardest_inner"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/hard_inner"),

                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/io"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/crafting_frame_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/item_frame_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/nullify_frame_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tank"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/null_bg"),

                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/dispense_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/vacuum_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/trashcan_bg_1"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/trashcan_bg_2"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/trashcan_bg_3"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/xp_bg"),

                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tele_dispense_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tele_vacuum")
        );

        for (ResourceLocation rl : tex)
            event.getMap().registerSprite(rl);
    }
}
