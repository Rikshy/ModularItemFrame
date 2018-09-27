package de.shyrik.modularitemframe.init;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.item.ItemModule;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.ModuleDispense;
import de.shyrik.modularitemframe.common.module.t2.ModuleTrashCan;
import de.shyrik.modularitemframe.common.module.t2.ModuleVacuum;
import de.shyrik.modularitemframe.common.module.t3.ModuleItemTeleporter;
import de.shyrik.modularitemframe.common.module.t3.ModuleXP;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RegistrarClient {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerAllItemModel(
            Item.getItemFromBlock(Blocks.MODULAR_FRAME),

            Items.SCREWDRIVER,
            Items.CANVAS,
            Items.MODULE,

            /*Items.MODULE_T1_ITEM,
            Items.MODULE_T1_CRAFT,
            Items.MODULE_T1_IO,
            Items.MODULE_T1_NULLIFY,
            Items.MODULE_T1_TANK,
            Items.MODULE_T1_STORAGE,
            Items.MODULE_T2_CRAFT_PLUS,
            Items.MODULE_T2_DISPENSE,
            Items.MODULE_T2_TRASHCAN,
            Items.MODULE_T2_VACUUM,
            Items.MODULE_T2_USE,
            Items.MODULE_T3_AUTO_CRAFTING,
            Items.MODULE_T3_FLUID_DISPENSER,
            Items.MODULE_T3_ITEMTELE,
            Items.MODULE_T3_TELE,
            Items.MODULE_T3_XP,*/

            Items.UPGRADE_SPEED,
            Items.UPGRADE_RANGE,
            Items.UPGRADE_CAPACITY,
            Items.UPGRADE_RESIST
        );

        ClientRegistry.bindTileEntitySpecialRenderer(TileModularFrame.class, new FrameRenderer());
    }

    @SubscribeEvent
    public static void registerTex(TextureStitchEvent.Pre event) {
        registerAllTex(event.getMap(),
                BlockModularFrame.INNER_HARDEST_LOC,
                BlockModularFrame.INNER_HARD_LOC,

                ModuleIO.BG_LOC,
                ModuleCrafting.BG_LOC,
                ModuleItem.BG_LOC,
                ModuleNullify.BG_LOC,
                ModuleTank.BG_LOC,

                ModuleDispense.BG_LOC,
                ModuleVacuum.BG_LOC,
                ModuleTrashCan.BG_LOC1,
                ModuleTrashCan.BG_LOC2,
                ModuleTrashCan.BG_LOC3,
                ModuleXP.BG_LOC,

                ModuleItemTeleporter.BG_IN,
                ModuleItemTeleporter.BG_OUT,
                ModuleItemTeleporter.BG_NONE
        );
    }

    private static void registerAllItemModel(Item... items) {
        for (Item item : items) {
            if (item instanceof ItemModule) {
                NonNullList<ItemStack> list = NonNullList.create();
                item.getSubItems(ModularItemFrame.TAB, list);
                list.forEach(stack -> registerItemModel(item, stack.getItemDamage(), ItemModule.getModuleId(stack), "inventory"));
            } else registerItemModel(item);
        }
    }

    private static void registerItemModel(Item item) {
        registerItemModel(item, 0, item.getRegistryName(), "inventory");
    }

    private static void registerItemModel(Item item, int meta, ResourceLocation location, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(location, variant));
    }

    private static void registerAllTex(TextureMap map, ResourceLocation... locations) {
        for (ResourceLocation tex : locations)
            map.registerSprite(tex);
    }
}
