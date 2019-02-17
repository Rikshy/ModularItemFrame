package de.shyrik.modularitemframe;

import de.shyrik.modularitemframe.client.gui.GuiHandler;
import de.shyrik.modularitemframe.common.module.t1.*;
import de.shyrik.modularitemframe.common.module.t2.*;
import de.shyrik.modularitemframe.common.module.t3.*;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.upgrade.UpgradeBlastResist;
import de.shyrik.modularitemframe.common.upgrade.UpgradeCapacity;
import de.shyrik.modularitemframe.common.upgrade.UpgradeRange;
import de.shyrik.modularitemframe.common.upgrade.UpgradeSpeed;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkRegistry;

import java.util.List;

@Mod(ModularItemFrame.MOD_ID)
public class ModularItemFrame {

    public static final String MOD_ID = "modularitemframe";
    public static final ResourceLocation CHANNEL = new ResourceLocation(MOD_ID, "??");

    public ModularItemFrame() {

    }
    public void addInformation(ItemTooltipEvent event) {
        List<ITextComponent> tooltip = event.getToolTip();

        boolean a = tooltip.stream().filter(text -> text.getString().equals("When in Main Hand")).findAny().map(tooltip::indexOf).get() == -1;
    }


    public void preInit(FMLPreInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        NetworkHandler.registerPackets();
    }
}
