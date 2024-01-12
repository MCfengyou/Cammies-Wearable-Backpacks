package dev.cammiescorner.camsbackpacks.neoforge.services;

import dev.cammiescorner.camsbackpacks.CamsBackpacks;
import dev.cammiescorner.camsbackpacks.util.platform.service.RegistryHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class NFRegistryHelper implements RegistryHelper {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CamsBackpacks.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CamsBackpacks.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CamsBackpacks.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CamsBackpacks.MOD_ID);

    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> createBlockEntityType(String name, Supplier<BlockEntityType.BlockEntitySupplier<T>> factory, Supplier<Block>[] supportedBlocks) {
        return BLOCK_ENTITY_TYPES.register(name, () -> {
            var blocks = new Block[supportedBlocks.length];
            for (int i = 0; i < supportedBlocks.length; i++) {
                blocks[i] = supportedBlocks[i].get();
            }
            //noinspection DataFlowIssue
            return BlockEntityType.Builder.of(factory.get(), blocks).build(null);
        });
    }

    @Override
    public Stream<Supplier<? extends Block>> getModBlocks() {
        return BLOCKS.getEntries().stream().map(UnaryOperator.identity());
    }

    @Override
    public <T extends Item> Supplier<T> createItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

    @Override
    public <T extends Block> Supplier<T> createBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    @Override
    public <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String name, Supplier<MenuExtendedFactory<T>> factory) {
        return MENU_TYPES.register(name, () -> IForgeMenuType.create((windowId, inv, data) -> factory.get().create(windowId, inv, data)));
    }
}
