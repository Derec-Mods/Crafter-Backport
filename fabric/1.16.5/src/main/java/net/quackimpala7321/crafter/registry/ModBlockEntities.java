package net.quackimpala7321.crafter.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.quackimpala7321.crafter.block.entity.CrafterBlockEntity;

public class ModBlockEntities {
    public static final BlockEntityType<CrafterBlockEntity> CRAFTER = Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            new Identifier("crafter", "crafter"),
            FabricBlockEntityTypeBuilder.create(CrafterBlockEntity::new, ModBlocks.CRAFTER).build(null)
    );

    public static void registerBlockEntities() {}
}
