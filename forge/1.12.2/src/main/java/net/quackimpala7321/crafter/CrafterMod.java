package net.quackimpala7321.crafter;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.quackimpala7321.crafter.network.PacketToggleSlot;
import net.quackimpala7321.crafter.proxy.CommonProxy;
import org.apache.logging.log4j.Logger;

@Mod(modid = CrafterMod.MODID, name = CrafterMod.NAME, version = CrafterMod.VERSION)
public class CrafterMod {
    public static final String MODID = "crafter";
    public static final String NAME = "Crafter";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MODID)
    public static CrafterMod instance;

    @SidedProxy(
        clientSide = "net.quackimpala7321.crafter.proxy.ClientProxy",
        serverSide = "net.quackimpala7321.crafter.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    public static Logger logger;
    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(PacketToggleSlot.Handler.class, PacketToggleSlot.class, 0, Side.SERVER);
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
