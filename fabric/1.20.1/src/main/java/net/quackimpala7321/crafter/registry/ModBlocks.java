package net.quackimpala7321.crafter.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.quackimpala7321.crafter.block.CrafterBlock;

public class ModBlocks {
    public static final Block CRAFTER = registerBlock(new Identifier("crafter", "crafter"),
            new CrafterBlock(FabricBlockSettings.of(Material.STONE)
                .requiresTool()
                .strength(1.5F, 3.5F)));

    private static Block registerBlock(Identifier id, Block block) {
        registerBlockItem(id, block);
        return Registry.register(Registry.BLOCK, id, block);
    }

    private static void registerBlockItem(Identifier id, Block block) {
        Registry.register(Registry.ITEM, id, new BlockItem(block, new FabricItemSettings().group(ItemGroup.REDSTONE)));
    }

    public static void registerBlocks() {}
}
