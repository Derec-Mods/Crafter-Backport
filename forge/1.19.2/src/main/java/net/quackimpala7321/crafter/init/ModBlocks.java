package net.quackimpala7321.crafter.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.quackimpala7321.crafter.CrafterMod;
import net.quackimpala7321.crafter.block.BlockCrafter;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

@Mod.EventBusSubscriber(modid = CrafterMod.MODID)
public class ModBlocks {
    public static final BlockCrafter CRAFTER = (BlockCrafter) new BlockCrafter()
            .setRegistryName(new ResourceLocation(CrafterMod.MODID, "crafter"))
            .setTranslationKey(CrafterMod.MODID + ".crafter");

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(CRAFTER);
        GameRegistry.registerTileEntity(TileEntityCrafter.class, new ResourceLocation(CrafterMod.MODID, "crafter"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        ItemBlock itemBlock = new ItemBlock(CRAFTER);
        itemBlock.setRegistryName(CRAFTER.getRegistryName());
        event.getRegistry().register(itemBlock);
    }
}
