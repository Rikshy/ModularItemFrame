package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler {

	@SubscribeEvent
	public static void onPlayerInteracted(PlayerInteractEvent.RightClickBlock event) {
		if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockModularFrame){
            event.setUseBlock(Event.Result.ALLOW);
        }
	}
}
