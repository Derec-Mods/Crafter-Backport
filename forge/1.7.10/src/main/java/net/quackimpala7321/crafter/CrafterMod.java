package net.quackimpala7321.crafter;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.quackimpala7321.crafter.block.BlockCrafter;
import net.quackimpala7321.crafter.network.PacketHandler;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

@Mod(modid = CrafterMod.MODID, name = "Crafter Backport", version = CrafterMod.VERSION)
public class CrafterMod {
    public static final String MODID = "crafter";
    public static final String VERSION = "1.0.1.6";

    @Mod.Instance(MODID)
    public static CrafterMod instance;

    @SidedProxy(
        clientSide = "net.quackimpala7321.crafter.ClientProxy",
        serverSide = "net.quackimpala7321.crafter.CommonProxy"
    )
    public static CommonProxy proxy;

    public static Block blockCrafter;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.init();
        blockCrafter = new BlockCrafter().setBlockName("crafter").setCreativeTab(CreativeTabs.tabRedstone);
        GameRegistry.registerBlock(blockCrafter, "crafter");
        GameRegistry.registerTileEntity(TileEntityCrafter.class, "TileEntityCrafter");
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        GameRegistry.addRecipe(
            new ItemStack(blockCrafter),
            "###",
            "#C#",
            "RDR",
            '#', Items.iron_ingot,
            'C', Blocks.crafting_table,
            'R', Items.redstone,
            'D', Blocks.dropper
        );
        proxy.init();
    }
}
