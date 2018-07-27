package de.shyrik.justcraftingframes.client.gui;

import com.teamwizardry.librarianlib.features.container.builtin.BaseWrappers;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.guicontainer.builtin.BaseLayouts;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import de.shyrik.justcraftingframes.JustCraftingFrames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiCraftingFrame extends GuiBase {

    static final Sprite FRAME_SPRITE = new Sprite(new ResourceLocation(JustCraftingFrames.MOD_ID, "textures/gui/crafting_frame.png"));

    private ComponentSprite tableComponent;
    private EntityPlayer player;

    public GuiCraftingFrame(EntityPlayer player) {
        super(250, 224);
        this.player = player;

        tableComponent = new ComponentSprite(FRAME_SPRITE, 0, 0, 250, 224);
        getMainComponents().add(tableComponent);

        BaseWrappers.InventoryWrapperPlayer iwp = BaseWrappers.INSTANCE.player(player);
        BaseLayouts.PlayerLayout pl = new BaseLayouts.PlayerLayout(iwp);

        getMainComponents().add(pl.getMain());
    }
}
