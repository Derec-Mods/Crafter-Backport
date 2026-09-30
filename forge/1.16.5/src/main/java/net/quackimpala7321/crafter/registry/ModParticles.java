package net.quackimpala7321.crafter.registry;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "crafter");

    public static final RegistryObject<DefaultParticleType> WHITE_SMOKE = PARTICLE_TYPES.register("white_smoke", 
            () -> new DefaultParticleType(false));
}
