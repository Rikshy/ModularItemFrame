package de.shyrik.modularitemframe;

import com.google.common.collect.ImmutableList;
import de.shyrik.modularitemframe.api.ItemModule;
import de.shyrik.modularitemframe.api.ModuleRegistry;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.item.ItemScrewdriver;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.ModuleTeleport;
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

    //@GameRegistry.ObjectHolder(ModularItemFrame.MOD_ID + ":modular_frame")
    public static BlockModularFrame FRAME_MODULAR;

    //Tier 1
    //@GameRegistry.ObjectHolder(ModularItemFrame.MOD_ID + ":module_t1_craft")
    public static ItemModule MODULE_CRAFT;
    public static ItemModule MODULE_IO;
    public static ItemModule MODULE_ITEM;
    public static ItemModule MODULE_NULL;
    public static ItemModule MODULE_TANK;

    //Tier 2
    public static ItemModule MODULE_CRAFTINGPLUS;
    public static ItemModule MODULE_DROP;
    public static ItemModule MODULE_XP;
    public static ItemModule MODULE_VACUUM;
    //public static ItemModule MODULE_TRASHCAN;

    //Tier 3
    public static ItemModule MODULE_TELE;
    //public static ItemModule MODULE_AUTOCRAFTING;

    public static ItemScrewdriver SCREWDRIVER;

    public static Item ITEM_CANVAS;

    public static List<Block> ALL_BLOCKS = ImmutableList.of(
            FRAME_MODULAR = new BlockModularFrame()
    );

    public static List<Item> ALL_ITEMS = ImmutableList.of(
            //Tier 1
            MODULE_CRAFT = ModuleRegistry.registerCreate( new ResourceLocation(ModularItemFrame.MOD_ID,"module_t1_craft"), ModuleCrafting.class),
            MODULE_IO = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_t1_io"), ModuleIO.class),
            MODULE_ITEM = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_t1_item"), ModuleItem.class),
            MODULE_NULL = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_t1_nullify"), ModuleNullify.class),
            MODULE_TANK = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_t1_tank"), ModuleTank.class),
            //Tier 2
            MODULE_CRAFTINGPLUS = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_t2_craft_plus"), ModuleCraftingPlus.class),
            MODULE_DROP = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_t2_dispense"), ModuleDispense.class),
            MODULE_XP = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_t2_xp"), ModuleXP.class),
            MODULE_VACUUM = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_t2_vacuum"), ModuleVacuum.class),
            //MODULE_TRASHCAN = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_trash_can"), ModuleTrashCan.class);

            //Tier 3
            //MODULE_AUTOCRAFTING = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_crafting_plus"), ModuleAutoCrafting.class);
            MODULE_TELE = ModuleRegistry.registerCreate(new ResourceLocation(ModularItemFrame.MOD_ID,"module_t3_tele"), ModuleTeleport.class),

            SCREWDRIVER = new ItemScrewdriver(),

            ITEM_CANVAS = new Item().setRegistryName(new ResourceLocation(ModularItemFrame.MOD_ID, "canvas")).setUnlocalizedName(ModularItemFrame.MOD_ID + ":canvas").setCreativeTab(ModularItemFrame.TAB)
    );

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for (Block block : ALL_BLOCKS)
            event.getRegistry().register(block);

        GameRegistry.registerTileEntity(TileModularFrame.class, new ResourceLocation(ModularItemFrame.MOD_ID,"modular_frame"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Block block : ALL_BLOCKS)
            event.getRegistry().register(new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName())));
        for (Item item : ALL_ITEMS)
            event.getRegistry().register(item);
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
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/crafting_frame_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/item_frame_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/nullify_frame_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/drop_frame_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/vacuum_bg"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/io"),
                new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tank")
        );

        for (ResourceLocation rl : tex)
            event.getMap().registerSprite(rl);
    }
}
