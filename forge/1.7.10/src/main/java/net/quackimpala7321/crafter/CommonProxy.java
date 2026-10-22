package net.quackimpala7321.crafter;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.quackimpala7321.crafter.inventory.ContainerCrafter;
import net.quackimpala7321.crafter.tileentity.TileEntityCrafter;

public class CommonProxy implements IGuiHandler {
    public void preInit() {}
    public void init() {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityCrafter) {
            return new ContainerCrafter(player.inventory, (TileEntityCrafter) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}
