package net.quackimpala7321.crafter.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

@SideOnly(Side.CLIENT)
public class EntityWhiteSmokeFX extends EntityFX {
    public EntityWhiteSmokeFX(World world, double x, double y, double z, int facing) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        ForgeDirection dir = ForgeDirection.getOrientation(facing);
        this.posX = x + dir.offsetX * 0.55D;
        this.posY = y + dir.offsetY * 0.55D;
        this.posZ = z + dir.offsetZ * 0.55D;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        this.motionX = dir.offsetX * 0.15D + (rand.nextDouble() - 0.5D) * 0.05D;
        this.motionY = dir.offsetY * 0.15D + (rand.nextDouble() - 0.5D) * 0.05D;
        this.motionZ = dir.offsetZ * 0.15D + (rand.nextDouble() - 0.5D) * 0.05D;
        this.particleRed = 0.729F;
        this.particleGreen = 0.694F;
        this.particleBlue = 0.761F;
        this.particleScale = 1.0F;
        this.particleMaxAge = 8 + this.rand.nextInt(4);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }
}
