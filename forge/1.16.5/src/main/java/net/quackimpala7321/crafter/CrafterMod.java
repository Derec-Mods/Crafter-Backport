package net.quackimpala7321.crafter;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import net.quackimpala7321.crafter.registry.ModBlocks;
import net.quackimpala7321.crafter.registry.ModBlockEntities;
import net.quackimpala7321.crafter.registry.ModScreenHandlers;
import net.quackimpala7321.crafter.registry.ModSoundEvents;
import net.quackimpala7321.crafter.registry.ModParticles;

import net.quackimpala7321.crafter.networking.ModMessages;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(CrafterMod.MOD_ID)
public class CrafterMod {
    public static final String MOD_ID = "crafter";

    public CrafterMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModScreenHandlers.SCREEN_HANDLERS.register(modEventBus);
        ModSoundEvents.SOUND_EVENTS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        
        modEventBus.addListener(this::commonSetup);
        
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(CrafterClientSetup::onClientSetup);
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }
}
