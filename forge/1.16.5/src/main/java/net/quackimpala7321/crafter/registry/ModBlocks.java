package net.quackimpala7321.crafter.registry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;
import net.quackimpala7321.crafter.block.CrafterBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "crafter");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "crafter");

    public static final RegistryObject<Block> CRAFTER = BLOCKS.register("crafter",
            () -> new CrafterBlock(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(1.5F, 3.5F)));

    public static final RegistryObject<Item> CRAFTER_ITEM = ITEMS.register("crafter",
            () -> new BlockItem(CRAFTER.get(), new Item.Settings().group(ItemGroup.REDSTONE)));
}
