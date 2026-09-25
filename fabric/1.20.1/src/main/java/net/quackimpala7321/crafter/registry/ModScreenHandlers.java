package net.quackimpala7321.crafter.registry;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.quackimpala7321.crafter.AutocrafterEarly;
import net.quackimpala7321.crafter.screen.CrafterScreenHandler;

public class ModScreenHandlers {
    public static final ScreenHandlerType<CrafterScreenHandler> CRAFTER_3X3 = Registry.register(
            Registry.SCREEN_HANDLER,
            new Identifier(AutocrafterEarly.MOD_ID, "crafter_3x3"),
            new ExtendedScreenHandlerType<>(CrafterScreenHandler::new));

    public static void registerScreenHandlers() {}
}
