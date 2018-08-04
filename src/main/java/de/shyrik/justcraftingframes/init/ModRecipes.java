package de.shyrik.justcraftingframes.init;

import de.shyrik.justcraftingframes.JustCraftingFrames;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModRecipes {
	public static void init() {
		GameRegistry.addShapedRecipe(ModBlocks.FRAME_CRAFTING.getRegistryName(), new ResourceLocation( JustCraftingFrames.MOD_ID ),
				new ItemStack(ModBlocks.FRAME_CRAFTING),
				"SSS",
				"SCS",
				"SSS",
				'S', "stickWood", 'C', Blocks.CRAFTING_TABLE);
	}
}
