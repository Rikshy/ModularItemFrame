package de.shyrik.modularitemframe.common.module.t2;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.util.FakePlayerHelper;
import de.shyrik.modularitemframe.api.util.FrameItemRenderer;
import de.shyrik.modularitemframe.api.util.ItemHandlerHelper;
import de.shyrik.modularitemframe.api.util.ItemHelper;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.function.Consumer;

public class ModuleUse extends ModuleBase implements Consumer<ItemStack> {
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_use");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "block/module_nyi");

    private static final GameProfile DEFAULT_CLICKER = new GameProfile(UUID.nameUUIDFromBytes("modularitemframe".getBytes()), "[Frame Clicker]");

    private static final String NBT_DISPLAY = "display";
    private static final String NBT_ROTATION = "rotation";
    private static final String NBT_SNEAK = "sneaking";
    private static final String NBT_RIGHT = "rightclick";

    private boolean isSneaking = false;
    private boolean rightClick = false;
    private int rotation = 0;
    private ItemStack displayItem = ItemStack.EMPTY;
    private WeakReference<FakePlayerHelper.UsefulFakePlayer> player;

    @Override
    public ResourceLocation getId() {
        return LOC;
    }

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void specialRendering(FrameRenderer tesr, @Nonnull MatrixStack matrixStack, float partialTicks, @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        Direction facing = tile.blockFacing();
        switch (facing) {
            case DOWN:
            case NORTH:
                FrameItemRenderer.renderOnFrame(displayItem, Direction.WEST, rotation, 0.5F, TransformType.FIRST_PERSON_RIGHT_HAND, matrixStack, buffer, combinedLight, combinedOverlay);
                break;
            case UP:
            case SOUTH:
                FrameItemRenderer.renderOnFrame(displayItem, Direction.EAST, rotation, 0.5F, TransformType.FIRST_PERSON_RIGHT_HAND, matrixStack, buffer, combinedLight, combinedOverlay);
                break;
            case WEST:
                matrixStack.rotate(new Quaternion(0, 90.0F, 0.0F, true));
                matrixStack.translate(-1, 0 ,0);
                FrameItemRenderer.renderOnFrame(displayItem, Direction.WEST, rotation, 0.5F, TransformType.FIRST_PERSON_RIGHT_HAND, matrixStack, buffer, combinedLight, combinedOverlay);
                break;
            case EAST:
                matrixStack.rotate(new Quaternion(0, 90.0F, 0.0F, true));
                matrixStack.translate(-1, 0 ,0);
                FrameItemRenderer.renderOnFrame(displayItem, Direction.EAST, rotation, 0.5F, TransformType.FIRST_PERSON_RIGHT_HAND, matrixStack, buffer, combinedLight, combinedOverlay);
                break;
        }
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.use");
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Direction facing, @Nullable PlayerEntity playerIn) {
        if (!worldIn.isRemote) ItemHelper.ejectStack(worldIn, pos, facing, displayItem);
    }

    @Override
    public ActionResultType onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, @Nonnull Direction facing, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (held.isEmpty()) {
                playerIn.setHeldItem(hand, displayItem.copy());
                displayItem.setCount(0);
            } else {
                if (displayItem.isEmpty()) {
                    displayItem = held.copy();
                    playerIn.setHeldItem(hand, ItemStack.EMPTY);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn, ItemStack driver) {
        if (!world.isRemote) {
            if (playerIn.isSneaking()) {
                isSneaking = !isSneaking;
            } else {
                rightClick = !rightClick;
            }
            String mode = isSneaking ? I18n.format("modularitemframe.mode.sn") + " + " : "";
            mode += rightClick ? I18n.format("modularitemframe.mode.rc") : I18n.format("modularitemframe.mode.lc");

            playerIn.sendMessage(new TranslationTextComponent("modularitemframe.message.mode_change", mode));
            tile.markDirty();
        }
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (!world.isRemote) {
            if (displayItem.isEmpty()) {
                displayItem = getNextStack();
                rotation = 0;
                tile.markDirty();
            } else {
                if (rotation >= 360) {
                    rotation -= 360;
                    hitIt(world, pos);
                }
                rotation += 15 * (tile.getSpeedUpCount() + 1);
                tile.markDirty();
            }
        }
    }

    private void hitIt(World world, BlockPos pos) {
        if (player == null) player = new WeakReference<>(FakePlayerHelper.getPlayer(world, DEFAULT_CLICKER));

        Direction facing = tile.blockFacing();
        FakePlayerHelper.setupFakePlayerForUse(getPlayer(), pos, facing, displayItem, isSneaking);
        ItemStack result;
        if (rightClick)
            result = FakePlayerHelper.rightClickInDirection(getPlayer(), world, pos.offset(facing), facing, world.getBlockState(pos), 2 + tile.getRangeUpCount());
        else
            result = FakePlayerHelper.leftClickInDirection(getPlayer(), world, pos.offset(facing), facing, world.getBlockState(pos), 2 + tile.getRangeUpCount());
        FakePlayerHelper.cleanupFakePlayerFromUse(player.get(), result, displayItem, this);
    }

    private ItemStack getNextStack() {
        IItemHandler handler = tile.getAttachedInventory();
        if (handler != null) {
            int slot = ItemHandlerHelper.getFirstOccupiedSlot(handler);
            if (slot >= 0) {
                return handler.extractItem(slot, handler.getStackInSlot(slot).getCount(), false);
            }
        }

        return ItemStack.EMPTY;
    }

    FakePlayerHelper.UsefulFakePlayer getPlayer() {
        return player.get();
    }

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cmp = super.serializeNBT();
        cmp.put(NBT_DISPLAY, displayItem.serializeNBT());
        cmp.putBoolean(NBT_SNEAK, isSneaking);
        cmp.putBoolean(NBT_RIGHT, rightClick);
        cmp.putInt(NBT_ROTATION, rotation);
        return cmp;
    }

    @Override
    public void deserializeNBT(CompoundNBT cmp) {
        super.deserializeNBT(cmp);
        if (cmp.contains(NBT_DISPLAY)) displayItem = ItemStack.read(cmp.getCompound(NBT_DISPLAY));
        if (cmp.contains(NBT_SNEAK)) isSneaking = cmp.getBoolean(NBT_SNEAK);
        if (cmp.contains(NBT_RIGHT)) rightClick = cmp.getBoolean(NBT_RIGHT);
        if (cmp.contains(NBT_ROTATION)) rotation = cmp.getInt(NBT_ROTATION);
    }

    @Override
    public void accept(ItemStack stack) {
        displayItem = stack;
    }
}
