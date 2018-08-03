package de.shyrik.justcraftingframes.client.gui;

import com.teamwizardry.librarianlib.features.container.InventoryWrapper;
import com.teamwizardry.librarianlib.features.container.builtin.BaseWrappers;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.guicontainer.ComponentSlot;
import com.teamwizardry.librarianlib.features.guicontainer.builtin.BaseLayouts;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import de.shyrik.justcraftingframes.JustCraftingFrames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.wrapper.InvWrapper;

public class GuiCraftingFrame extends GuiBase {

    static final Sprite FRAME_SPRITE = new Sprite(new ResourceLocation(JustCraftingFrames.MOD_ID, "textures/gui/crafting_frame.png"));

    private ComponentSprite tableComponent;
    private EntityPlayer player;

    public GuiCraftingFrame(EntityPlayer player) {
        super(175, 165);
        this.player = player;

        tableComponent = new ComponentSprite(FRAME_SPRITE, 0, 0, 175, 165);
        getMainComponents().add(tableComponent);


        ComponentVoid pInventWrap = new ComponentVoid(7,83);
        BaseWrappers.InventoryWrapperPlayer iwp = BaseWrappers.INSTANCE.player(player);
        ComponentVoid pInvent = new ComponentVoid(0, 0);
        for ( int row = 0; row < 2; row++) {
            for ( int col = 0; col < 8; col++) {
                pInvent.add(new ComponentSlot(iwp.getMain().get(row * 9 + col), col * 18, row * 18));
            }
        }
        ComponentVoid hotbar = new ComponentVoid(0, 58);
        for ( int col = 0; col < 8; col++) {
            hotbar.add(new ComponentSlot(iwp.getHotbar().get(col), col * 18, 0));
        }
        pInvent.add(hotbar);
        pInvent.clipping.setClipToBounds(true);
        pInventWrap.add(pInvent);

        getMainComponents().add(pInventWrap);
    }
}
