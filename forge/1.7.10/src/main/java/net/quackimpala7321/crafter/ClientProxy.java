package net.quackimpala7321.crafter;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.quackimpala7321.crafter.block.CrafterBlockRenderer;
import net.quackimpala7321.crafter.client.gui.GuiCrafter;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class ClientProxy extends CommonProxy {
    public static int crafterRenderId;

    @Override
    public void preInit() {
        crafterRenderId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new CrafterBlockRenderer());
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCrafter) {
            return new GuiCrafter(player.inventory, (TileEntityCrafter) te);
        }
        return null;
    }
}
