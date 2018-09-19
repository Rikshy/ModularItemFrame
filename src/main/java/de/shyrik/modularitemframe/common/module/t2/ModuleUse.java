package de.shyrik.modularitemframe.common.module.t2;

import com.mojang.authlib.GameProfile;
import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.FakePlayerUtils;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.function.Consumer;

public class ModuleUse extends ModuleBase implements Consumer<ItemStack> {
    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t2_use");
    public static final ResourceLocation BG_LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t1_item");

    private static final GameProfile DEFAULT_CLICKER = new GameProfile(UUID.nameUUIDFromBytes("modularitemframe".getBytes()), "[Frame Clicker]");

    private static final String NBT_DISPLAY = "display";
    private static final String NBT_ROTATION = "rotation";
    private static final String NBT_SNEAK = "sneaking";
    private static final String NBT_RIGHT = "rightclick";

    private boolean isSneaking = false;
    private boolean rightClick = false;
    private int rotation = 0;
    private ItemStack displayItem = ItemStack.EMPTY;
    private WeakReference<FakePlayerUtils.UsefulFakePlayer> player;

    @Nonnull
    @Override
    public ResourceLocation frontTexture() {
        return BG_LOC;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void specialRendering(FrameRenderer tesr, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.pushMatrix();

        EnumFacing facing = tile.blockFacing();
        switch (facing) {
            case DOWN:
                GlStateManager.rotate(rotation, 1.0F, 0.0F, 0.0F);
                RenderUtils.renderItem(displayItem, facing, 180.0F, 0.5F, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
                break;
            case UP:
                GlStateManager.rotate(rotation, 1.0F, 0.0F, 0.0F);
                RenderUtils.renderItem(displayItem, facing, 180.0F, 0.5F, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
                break;
            case NORTH:
                GlStateManager.rotate(rotation, 1.0F, 0.0F, 0.0F);
                RenderUtils.renderItem(displayItem, facing, 180.0F, 0.5F, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
                break;
            case SOUTH:
                GlStateManager.rotate(rotation * -1, 1.0F, 0.0F, 0.0F);
                RenderUtils.renderItem(displayItem, facing, 180.0F, 0.5F, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
                break;
            case WEST:
                GlStateManager.rotate(rotation * -1, 0.0F, 0.0F, 1.0F);
                RenderUtils.renderItem(displayItem, facing, 180.0F, 0.5F, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
                break;
            case EAST:
                GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
                RenderUtils.renderItem(displayItem, facing, 180.0F, 0.5F, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
                break;
        }

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public String getModuleName() {
        return null;
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
        if (playerIn instanceof FakePlayer && !ConfigValues.AllowFakePlayers) return;

        if (!world.isRemote) {
            if (playerIn.isSneaking()) {
                isSneaking = !isSneaking;
            } else {
                rightClick = !rightClick;
            }
            String mode = isSneaking ? I18n.format("modularitemframe.mode.sn") + " + " : "";
            mode += rightClick ? I18n.format("modularitemframe.mode.rc") : I18n.format("modularitemframe.mode.lc");

            playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.mode_change", mode));
            tile.markDirty();
        }
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if(!world.isRemote && !tile.isPowered()) {
            TileEntity neighbor = tile.getAttachedTile();
            if (neighbor != null) {
                EnumFacing blockFacing = tile.blockFacing();
                IItemHandler handler = neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing);
                if (handler != null) {
                    int slot = ItemUtils.getFirstOccupiedSlot(handler);
                    if (slot >= 0) {
                        ItemStack slotStack = handler.getStackInSlot(slot);
                        if (!ItemStack.areItemsEqual(slotStack, displayItem)) {
                            displayItem = slotStack;
                            rotation = 0;
                            tile.markDirty();
                        } else {
                            if (!displayItem.isEmpty()) {
                                if (rotation == 360) {
                                    rotation = 0;
                                }
                                if (rotation % 180 == 0)
                                    hitIt(world, pos);
                                rotation += 10;
                                tile.markDirty();
                            }
                        }
                    }
                }
            }
        }
    }

    private void hitIt(World world, BlockPos pos) {
        if (player == null) player = new WeakReference<>(FakePlayerUtils.getPlayer(world, DEFAULT_CLICKER));

        EnumFacing facing = tile.blockFacing().getOpposite();
        FakePlayerUtils.setupFakePlayerForUse(player.get(), pos, facing, displayItem, isSneaking);
        ItemStack result;
        if(!rightClick) result = FakePlayerUtils.leftClickInDirection(player.get(), world, pos, facing, world.getBlockState(pos));
        else result = FakePlayerUtils.rightClickInDirection(player.get(), world, pos, facing, world.getBlockState(pos));
        FakePlayerUtils.cleanupFakePlayerFromUse(player.get(), result, displayItem, this);
    }

    @Override
    public void additionalUpdateNBT(@Nonnull NBTTagCompound cmp) {
        cmp.setInteger(NBT_ROTATION, rotation); //TODO
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setTag(NBT_DISPLAY, displayItem.serializeNBT());
        compound.setBoolean(NBT_SNEAK, isSneaking);
        compound.setBoolean(NBT_RIGHT, rightClick);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_DISPLAY)) displayItem = new ItemStack(nbt.getCompoundTag(NBT_DISPLAY));
        if (nbt.hasKey(NBT_ROTATION)) rotation = nbt.getInteger(NBT_ROTATION);
        if (nbt.hasKey(NBT_SNEAK)) isSneaking = nbt.getBoolean(NBT_SNEAK);
        if (nbt.hasKey(NBT_RIGHT)) rightClick = nbt.getBoolean(NBT_RIGHT);
    }

    @Override
    public void accept(ItemStack stack) {
        TileEntity neighbor = tile.getAttachedTile();
        if (neighbor != null) {
            EnumFacing blockFacing = tile.blockFacing();
            IItemHandlerModifiable handler = (IItemHandlerModifiable) neighbor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, blockFacing);
            if (handler != null) {
                int slot = ItemUtils.getFirstOccupiedSlot(handler);
                if (slot >= 0) {
                    handler.setStackInSlot(slot, stack);
                }
            }
        }
    }
}
