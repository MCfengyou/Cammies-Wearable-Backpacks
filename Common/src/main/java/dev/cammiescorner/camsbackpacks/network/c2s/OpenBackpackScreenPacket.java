package dev.cammiescorner.camsbackpacks.network.c2s;

import dev.cammiescorner.camsbackpacks.config.BackpacksConfig;
import dev.cammiescorner.camsbackpacks.menu.BackpackMenu;
import dev.cammiescorner.camsbackpacks.util.platform.Services;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;

public class OpenBackpackScreenPacket {

    public static void send() {
        throw new UnsupportedOperationException();
    }

    public static void handle(ServerPlayer player) {
        if (!BackpacksConfig.allowInventoryGui) {
            player.sendSystemMessage(Component.translatable("error.camsbackpacks.chest_slot_ui_disabled"), true);
            return;
        }

        final NonNullList<ItemStack> stacks = NonNullList.withSize(36, ItemStack.EMPTY);
        ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
        CompoundTag tag = stack.getOrCreateTag();
        ContainerHelper.loadAllItems(tag, stacks);
        Container inventory = new Container() {
            @Override
            public void clearContent() {
                stacks.clear();
            }

            @Override
            public int getContainerSize() {
                return stacks.size();
            }

            @Override
            public boolean isEmpty() {
                return stacks.isEmpty();
            }

            @Override
            public ItemStack getItem(int slot) {
                return stacks.get(slot);
            }

            @Override
            public ItemStack removeItem(int slot, int amount) {
                return ContainerHelper.removeItem(stacks, slot, amount);
            }

            @Override
            public ItemStack removeItemNoUpdate(int slot) {
                return ContainerHelper.takeItem(stacks, slot);
            }

            @Override
            public void setItem(int slot, ItemStack stack) {
                stacks.set(slot, stack);
            }

            @Override
            public void setChanged() {

            }

            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        };

        Services.MENU.openMenu(player, stack, inventory);
    }
}
