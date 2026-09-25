package net.quackimpala7321.crafter.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.quackimpala7321.crafter.CrafterMod;

@Mod.EventBusSubscriber(modid = CrafterMod.MODID)
public class ModSounds {
    public static SoundEvent CRAFTER_CRAFT;
    public static SoundEvent CRAFTER_FAIL;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        CRAFTER_CRAFT = registerSound(event, "crafter_craft");
        CRAFTER_FAIL = registerSound(event, "crafter_fail");
    }

    private static SoundEvent registerSound(RegistryEvent.Register<SoundEvent> event, String name) {
        ResourceLocation loc = new ResourceLocation(CrafterMod.MODID, name);
        SoundEvent sound = new SoundEvent(loc).setRegistryName(loc);
        event.getRegistry().register(sound);
        return sound;
    }
}
