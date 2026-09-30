package net.quackimpala7321.crafter.registry;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;
import net.quackimpala7321.crafter.block.entity.CrafterBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, "crafter");

    public static final RegistryObject<BlockEntityType<CrafterBlockEntity>> CRAFTER = BLOCK_ENTITIES.register("crafter",
            () -> BlockEntityType.Builder.create(CrafterBlockEntity::new, ModBlocks.CRAFTER.get()).build(null));
}
