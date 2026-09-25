package net.quackimpala7321.crafter.registry;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.quackimpala7321.crafter.CrafterMod;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CrafterMod.MOD_ID);

    public static final RegistryObject<DefaultParticleType> WHITE_SMOKE = PARTICLE_TYPES.register("white_smoke", 
            () -> new DefaultParticleType(false));
}
