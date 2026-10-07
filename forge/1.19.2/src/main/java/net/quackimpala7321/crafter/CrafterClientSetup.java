package net.quackimpala7321.crafter;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.quackimpala7321.crafter.registry.ModParticles;
import net.quackimpala7321.crafter.registry.ModScreenHandlers;
import net.quackimpala7321.crafter.screen.CrafterScreen;

public class CrafterClientSetup {
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            HandledScreens.register(ModScreenHandlers.CRAFTER_3X3.get(), CrafterScreen::new);
        });
    }

    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.register(ModParticles.WHITE_SMOKE.get(), WhiteSmokeParticle.Factory::new);
    }
}
