package net.quackimpala7321.crafter.registry;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.quackimpala7321.crafter.AutocrafterEarly;

public class ModSoundEvents {
    public static final SoundEvent CRAFTER_CRAFT = register("crafter_craft");
    public static final SoundEvent CRAFTER_FAIL = register("crafter_fail");

    private static SoundEvent register(String name) {
        final Identifier id = new Identifier(AutocrafterEarly.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void registerSounds() {}
}
