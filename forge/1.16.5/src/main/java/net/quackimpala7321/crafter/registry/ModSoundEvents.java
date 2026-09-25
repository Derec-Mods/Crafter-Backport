package net.quackimpala7321.crafter.registry;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "crafter");

    public static final RegistryObject<SoundEvent> CRAFTER_CRAFT = SOUND_EVENTS.register("crafter_craft", 
            () -> new SoundEvent(new Identifier("crafter", "crafter_craft")));
            
    public static final RegistryObject<SoundEvent> CRAFTER_FAIL = SOUND_EVENTS.register("crafter_fail", 
            () -> new SoundEvent(new Identifier("crafter", "crafter_fail")));
}
