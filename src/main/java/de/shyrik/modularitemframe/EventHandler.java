package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class EventHandler {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerTex(TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/crafting_frame_bg"));
		event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/item_frame_bg"));
		event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/nullify_frame_bg"));
		event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/tank_frame_bg"));
		event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/drop_frame_bg"));
		event.getMap().registerSprite(new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/vacuum_bg"));
	}

	@SubscribeEvent
	public static void onPlayerInteracted(PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockModularFrame){
            event.setUseBlock(Event.Result.ALLOW);
        }
	}
}
