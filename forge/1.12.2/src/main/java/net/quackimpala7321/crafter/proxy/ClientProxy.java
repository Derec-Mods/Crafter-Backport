package net.quackimpala7321.crafter.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.quackimpala7321.crafter.client.gui.GuiCrafter;
import net.quackimpala7321.crafter.init.ModBlocks;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelLoader.setCustomModelResourceLocation(
            Item.getItemFromBlock(ModBlocks.CRAFTER),
            0,
            new ModelResourceLocation(ModBlocks.CRAFTER.getRegistryName(), "inventory")
        );
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_CRAFTER) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityCrafter) {
                return new GuiCrafter(player.inventory, (TileEntityCrafter) te);
            }
        }
        return null;
    }
}
