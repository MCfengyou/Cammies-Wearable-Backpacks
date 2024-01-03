package dev.cammiescorner.camsbackpacks.common.blocks.entities;

import dev.cammiescorner.camsbackpacks.common.menu.BackpackMenu;
import dev.cammiescorner.camsbackpacks.core.registry.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BackpackBlockEntity extends BlockEntity implements Container, Nameable, ExtendedScreenHandlerFactory {
    public final NonNullList<ItemStack> inventory = NonNullList.withSize(36, ItemStack.EMPTY);
    public boolean wasPickedUp = false;
    private Component name = Component.nullToEmpty("");

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BACKPACK, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, inventory);
        wasPickedUp = tag.getBoolean("PickedUp");
        if (tag.contains("CustomName", Tag.TAG_STRING))
            this.name = Component.Serializer.fromJson(tag.getString("CustomName"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, inventory);
        tag.putBoolean("PickedUp", wasPickedUp);

        if (this.name != null)
            tag.putString("CustomName", Component.Serializer.toJson(this.name));

        super.saveAdditional(tag);
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(inventory, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return !(player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) > 64.0D);
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }

    @Override
    public Component getName() {
        return name;
    }

    public void setName(Component text) {
        this.name = text;
        this.setChanged();
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new BackpackMenu(syncId, inv, this, ContainerLevelAccess.create(player.level(), getBlockPos()), getBlockPos(), true);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(getBlockPos());
        buf.writeBoolean(true);
    }
}
