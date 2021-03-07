package de.shyrik.modularitemframe.common.module.t3;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.client.FrameRenderer;
import de.shyrik.modularitemframe.common.block.ModularFrameBlock;
import de.shyrik.modularitemframe.api.Inventory.ItemStackHandlerWrapper;
import de.shyrik.modularitemframe.api.Inventory.OpenItemStackHandler;
import de.shyrik.modularitemframe.util.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class JukeboxModule  extends ModuleBase {
    public static final ResourceLocation ID = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_jukebox");
    public static final ResourceLocation BG = new ResourceLocation(ModularItemFrame.MOD_ID, "module/module_t3_jukebox");
    private static final TextComponent NAME = new TranslationTextComponent("modularitemframe.module.jukebox");

    private static final String NBT_JUKEBOX = "jukebox";
    private static final String NBT_CURRENT = "current_song";
    private static final String NBT_LAST = "last_click";

    private final ItemStackHandlerWrapper jukebox = new ItemStackHandlerWrapper(new OpenItemStackHandler(9));
    private int currentSong = -1;
    private long lastClick;
    private int rotation = 0;

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    public ResourceLocation frontTexture() {
        return BG;
    }

    @NotNull
    @Override
    public ResourceLocation innerTexture() {
        return ModularFrameBlock.INNER_HARDEST;
    }

    @NotNull
    @Override
    public TextComponent getName() {
        return NAME;
    }

    @Override
    public void specialRendering(@NotNull FrameRenderer renderer, float ticks, @NotNull MatrixStack matrixStack, @NotNull IRenderTypeBuffer buffer, int light, int overlay) {
        if (currentSong >= 0 && currentSong < jukebox.getSlots()) {
            renderer.renderInside(jukebox.getStackInSlot(currentSong), -rotation, matrixStack, buffer, light, overlay);
        }
    }

    @Override
    public ActionResultType onUse(@NotNull World world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull Direction facing, BlockRayTraceResult trace) {
        if (!world.isRemote) {
            ItemStack held = player.getHeldItem(hand);
            if (held.getItem() instanceof MusicDiscItem) {
                if (jukebox.insert(held, true).isEmpty()) {
                    jukebox.insert(held.split(1), false);
                    markDirty();
                }
            } else if (held.isEmpty() && player.isSneaking()) {
                ItemStack ejectStack;
                if (currentSong >= 0) {
                    ejectStack = jukebox.extractStackInSlot(currentSong);
                    stop(world);
                } else {
                    ejectStack = jukebox.extract(false);
                }
                if (!ejectStack.isEmpty())
                    ItemHelper.ejectStack(world, pos, frame.getFacing(), ejectStack);
                markDirty();
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onBlockClicked(@NotNull World world, @NotNull BlockPos pos, @NotNull PlayerEntity player) {
        if (world.isRemote) return;
        if (player.isSneaking()) {
            long time = world.getGameTime();
            if (time - lastClick <= 10L) {
                playPrevious(world);
            } else if (currentSong >= 0) {
                play(world, jukebox.getStackInSlot(currentSong));
            }
            lastClick = time;
        } else {
            playNext(world);
        }
        markDirty();
    }

    @Override
    public void tick(@NotNull World world, @NotNull BlockPos pos) {
        if (world.isRemote) {
            if (rotation >= 360) {
                rotation = 0;
            } else {
                rotation += 5;
            }
        }
    }

    @Override
    public void onFrameUpgradesChanged(World world, BlockPos pos, Direction facing) {
        int newCapacity = 9 * (frame.getCapacityUpCount() + 1);

        if (newCapacity != jukebox.getSlots()) {
            IItemHandler cpy = jukebox.copy().getHandler();
            jukebox.setSize(newCapacity);
            for (int slot = 0; slot < cpy.getSlots(); slot++) {
                if (slot < jukebox.getSlots())
                    jukebox.insert(cpy.getStackInSlot(slot), false);
                else
                    ItemHelper.ejectStack(world, pos, facing, cpy.getStackInSlot(slot));
            }

            if (currentSong >= jukebox.getSlots()) {
                stop(world);
            }

            markDirty();
        }
    }

    @Override
    public void onRemove(@NotNull World world, @NotNull BlockPos pos, @NotNull Direction facing, PlayerEntity player, @NotNull ItemStack moduleStack) {
        stop(world);
    }

    @NotNull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = super.serializeNBT();
        tag.put(NBT_JUKEBOX, jukebox.serializeNBT());
        tag.putInt(NBT_CURRENT, currentSong);
        tag.putLong(NBT_LAST, lastClick);
        return tag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundNBT tag) {
        super.deserializeNBT(tag);
        if (tag.contains(NBT_JUKEBOX)) jukebox.deserializeNBT(tag.getCompound(NBT_JUKEBOX));
        if (tag.contains(NBT_CURRENT)) currentSong = tag.getInt(NBT_CURRENT);
        if (tag.contains(NBT_LAST)) lastClick = tag.getLong(NBT_LAST);
    }

    private void stop(World world) {
        currentSong = -1;
        world.playEvent(1010, frame.getPos(), 0);
    }

    private void play(World world, ItemStack songStack) {
        world.playEvent(1010, frame.getPos(), Item.getIdFromItem(songStack.getItem()));
    }

    private void playNext(World world) {
        int prevSong = Math.min(currentSong, jukebox.getSlots());
        ItemStack songStack;
        do {
            if (currentSong + 1 >= jukebox.getSlots()) {
                currentSong = -1;
            }
            songStack = jukebox.getStackInSlot(++currentSong);
        } while (songStack.isEmpty() && prevSong != currentSong);

        if (songStack.isEmpty())
            return;

        if (prevSong != currentSong)
            play(world, songStack);
    }

    private void playPrevious(World world) {
        int prevSong = Math.max(currentSong, 0);
        ItemStack songStack;
        do {
            if (currentSong - 1 < 0) {
                currentSong = jukebox.getSlots();
            }
            songStack = jukebox.getStackInSlot(--currentSong);
        } while (songStack.isEmpty() && prevSong != currentSong);

        if (songStack.isEmpty())
            return;

        if (prevSong != currentSong)
            play(world, songStack);
    }
}
