package de.shyrik.modularitemframe.common.module.t3;

import de.shyrik.modularitemframe.ModularItemFrame;
import de.shyrik.modularitemframe.api.ConfigValues;
import de.shyrik.modularitemframe.api.ModuleBase;
import de.shyrik.modularitemframe.api.utils.ItemUtils;
import de.shyrik.modularitemframe.api.utils.RenderUtils;
import de.shyrik.modularitemframe.client.render.FrameRenderer;
import de.shyrik.modularitemframe.common.block.BlockModularFrame;
import de.shyrik.modularitemframe.common.network.NetworkHandler;
import de.shyrik.modularitemframe.common.network.packet.SpawnParticlesPacket;
import de.shyrik.modularitemframe.common.tile.TileModularFrame;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ModuleItemTeleporter extends ModuleBase {

    public static final ResourceLocation LOC = new ResourceLocation(ModularItemFrame.MOD_ID, "module_t3_itemtele");
    public static final ResourceLocation BG_IN = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t3_itemtelein");
    public static final ResourceLocation BG_OUT = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t3_itemteleout");
    public static final ResourceLocation BG_NONE = new ResourceLocation(ModularItemFrame.MOD_ID, "blocks/module_t3_itemtelenone");

    private static final String NBT_LINK = "item_linked_pos";
    private static final String NBT_LINKX = "linked_posX";
    private static final String NBT_LINKY = "linked_posY";
    private static final String NBT_LINKZ = "linked_posZ";
    private static final String NBT_DIR = "direction";

    private BlockPos linkedLoc = null;
    private EnumMode direction = EnumMode.NONE;

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation frontTexture() {
        switch (direction) {
            case VACUUM:
                return BG_IN;
            case DISPENSE:
                return BG_OUT;
            case NONE:
                return BG_NONE;
        }
        return BG_NONE;
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation innerTexture() {
        return BlockModularFrame.INNER_HARDEST_LOC;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void specialRendering(FrameRenderer renderer, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(direction == EnumMode.NONE) {
            RenderUtils.renderEnd(renderer, x, y, z, info -> {
                switch (tile.blockFacing()) {
                    case DOWN:
                        info.buffer.pos(x + 0.7d, y + 0.08d, z + 0.7d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.7d, y + 0.08d, z + 0.3d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.3d, y + 0.08d, z + 0.3d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.3d, y + 0.08d, z + 0.7d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case UP:
                        info.buffer.pos(x + 0.7d, y + 0.92d, z + 0.3d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.7d, y + 0.92d, z + 0.7d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.3d, y + 0.92d, z + 0.7d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.3d, y + 0.92d, z + 0.3d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case NORTH:
                        info.buffer.pos(x + 0.7d, y + 0.7d, z + 0.08d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.3d, y + 0.7d, z + 0.08d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.3d, y + 0.3d, z + 0.08d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.7d, y + 0.3d, z + 0.08d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case SOUTH:
                        info.buffer.pos(x + 0.3d, y + 0.7d, z + 0.92d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.7d, y + 0.7d, z + 0.92d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.7d, y + 0.3d, z + 0.92d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.3d, y + 0.3d, z + 0.92d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case WEST:
                        info.buffer.pos(x + 0.08d, y + 0.7d, z + 0.3d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.08d, y + 0.7d, z + 0.7d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.08d, y + 0.3d, z + 0.7d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.08d, y + 0.3d, z + 0.3d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                    case EAST:
                        info.buffer.pos(x + 0.92d, y + 0.7d, z + 0.7d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.92d, y + 0.7d, z + 0.3d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.92d, y + 0.3d, z + 0.3d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        info.buffer.pos(x + 0.92d, y + 0.3d, z + 0.7d).color(info.color1, info.color2, info.color3, 1.0F).endVertex();
                        break;
                }
                return true;
            });
        }
    }

    @Override
    public String getModuleName() {
        return I18n.format("modularitemframe.module.itemtele");
    }

    @Override
    public void screw(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer playerIn, ItemStack driver) {
        NBTTagCompound nbt = driver.getTagCompound();
        if (playerIn.isSneaking()) {
            if (nbt == null) nbt = new NBTTagCompound();
            nbt.setLong(NBT_LINK, tile.getPos().toLong());
            driver.setTagCompound(nbt);
            playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.loc_saved"));
        } else {
            if (nbt != null && nbt.hasKey(NBT_LINK)) {
                BlockPos tmp = BlockPos.fromLong(nbt.getLong(NBT_LINK));
                TileEntity targetTile = tile.getWorld().getTileEntity(tmp);
                int countRange = tile.getRangeUpCount();
                if (!(targetTile instanceof TileModularFrame) || !((((TileModularFrame) targetTile).module instanceof ModuleItemTeleporter)))
                    playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.invalid_target"));
                else if (tile.getPos().getDistance(tmp.getX(), tmp.getY(), tmp.getZ()) > ConfigValues.BaseTeleportRange + (countRange * 10)) {
                    playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.too_far", ConfigValues.BaseTeleportRange + (countRange * 10)));
                } else {
                    linkedLoc = tmp;
                    direction = EnumMode.DISPENSE;
                    reloadModel = true;

                    ModuleItemTeleporter targetModule = (ModuleItemTeleporter) ((TileModularFrame) targetTile).module;
                    targetModule.linkedLoc = tile.getPos();
                    targetModule.direction = EnumMode.VACUUM;
                    targetModule.reloadModel = true;

                    playerIn.sendMessage(new TextComponentTranslation("modularitemframe.message.link_established"));
                    nbt.removeTag(NBT_LINK);
                    driver.setTagCompound(nbt);

                    targetTile.markDirty();
                    tile.markDirty();
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public void onRemove(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nullable EntityPlayer playerIn) {
        if (hasValidConnection(worldIn)) {
            ModuleItemTeleporter targetModule = (ModuleItemTeleporter) ((TileModularFrame) Objects.requireNonNull(worldIn.getTileEntity(linkedLoc))).module;
            targetModule.linkedLoc = null;
            targetModule.direction = EnumMode.NONE;
            targetModule.reloadModel = true;
        }
    }

    @Override
    public void tick(@Nonnull World world, @Nonnull BlockPos pos) {
        if (direction != EnumMode.VACUUM) return;
        if (!hasValidConnection(world)) return;
        if (world.getTotalWorldTime() % (60 - 10 * tile.getSpeedUpCount()) != 0) return;

        List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, getVacuumBB(pos));
        for (EntityItem entity : entities) {
            ItemStack entityStack = entity.getItem();
            if (entity.isDead || entityStack.isEmpty()) continue;

            ItemUtils.ejectStack(world, linkedLoc, world.getBlockState(pos).getValue(BlockModularFrame.FACING), entityStack);
            entity.setDead();
            NetworkHandler.sendAround(new SpawnParticlesPacket(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), entity.getPosition(), 1), entity.getPosition(), entity.dimension);
            break;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "theoneprobe")
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        super.addProbeInfo(mode, probeInfo, player, world, blockState, data);
        probeInfo.horizontal().text(I18n.format("modularitemframe.tooltip.tele_valid", hasValidConnection(world)));
        probeInfo.horizontal().text(I18n.format("modularitemframe.tooltip.mode", direction.getName()));
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "waila")
    public List<String> getWailaBody(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        List<String> tooltips = super.getWailaBody(itemStack, accessor, config);
        tooltips.add(I18n.format("modularitemframe.tooltip.tele_valid", hasValidConnection(accessor.getWorld())));
        tooltips.add(I18n.format("modularitemframe.tooltip.mode", direction.getName()));
        return tooltips;
    }

    @Nonnull
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        if (linkedLoc != null) {
            compound.setInteger(NBT_LINKX, linkedLoc.getX());
            compound.setInteger(NBT_LINKY, linkedLoc.getY());
            compound.setInteger(NBT_LINKZ, linkedLoc.getZ());
            compound.setInteger(NBT_DIR, direction.index);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        if (nbt.hasKey(NBT_LINKX))
            linkedLoc = new BlockPos(nbt.getInteger(NBT_LINKX), nbt.getInteger(NBT_LINKY), nbt.getInteger(NBT_LINKZ));
        if (nbt.hasKey(NBT_DIR)) direction = EnumMode.values()[nbt.getInteger(NBT_DIR)];
    }

    private boolean hasValidConnection(@Nonnull World world) {
        if (linkedLoc == null) return false;
        TileEntity tile = world.getTileEntity(linkedLoc);
        if (!(tile instanceof TileModularFrame)
                || !(((TileModularFrame) tile).module instanceof ModuleItemTeleporter)
                || ((ModuleItemTeleporter) ((TileModularFrame) tile).module).direction != EnumMode.DISPENSE)
            return false;
        return true;
    }

    private AxisAlignedBB getVacuumBB(@Nonnull BlockPos pos) {
        int range = ConfigValues.BaseVacuumRange + tile.getRangeUpCount();
        switch (tile.blockFacing()) {
            case DOWN:
                return new AxisAlignedBB(pos.add(-5, 0, -5), pos.add(5, 5, 5));
            case UP:
                return new AxisAlignedBB(pos.add(-5, 0, -5), pos.add(5, -5, 5));
            case NORTH:
                return new AxisAlignedBB(pos.add(-5, -5, 0), pos.add(5, 5, 5));
            case SOUTH:
                return new AxisAlignedBB(pos.add(-5, -5, 0), pos.add(5, 5, -5));
            case WEST:
                return new AxisAlignedBB(pos.add(0, -5, -5), pos.add(-5, 5, 5));
            case EAST:
                return new AxisAlignedBB(pos.add(0, -5, -5), pos.add(5, 5, 5));
        }
        return new AxisAlignedBB(pos, pos.add(1, 1, 1));
    }


    public enum EnumMode {
        VACUUM(0, "modularitemframe.mode.in"),
        DISPENSE(1, "modularitemframe.mode.out"),
        NONE(2, "modularitemframe.mode.no");

        private final int index;
        private final String name;

        EnumMode(int indexIn, String nameIn) {
            index = indexIn;
            name = nameIn;
        }

        public int getIndex() {
            return this.index;
        }

        public String getName() {
            return I18n.format(this.name);
        }
    }

}
