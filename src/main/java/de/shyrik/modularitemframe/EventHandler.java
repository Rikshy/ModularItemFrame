package de.shyrik.modularitemframe;

import com.google.common.collect.ImmutableList;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Mod.EventBusSubscriber
public class EventHandler {

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

	@SubscribeEvent
	public static void onPlayerInteracted(PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockModularFrame){
            event.setUseBlock(Event.Result.ALLOW);
        }
	}
}
