package net.quackimpala7321.crafter;

import net.fabricmc.api.ModInitializer;
import net.quackimpala7321.crafter.registry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutocrafterEarly implements ModInitializer {
    public static final String MOD_ID = "crafter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModScreenHandlers.registerScreenHandlers();
		ModBlocks.registerBlocks();
		ModBlockEntities.registerBlockEntities();
		ModParticles.registerParticles();
		ModSoundEvents.registerSounds();
	}
}
