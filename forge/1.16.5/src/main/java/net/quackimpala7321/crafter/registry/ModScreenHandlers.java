package net.quackimpala7321.crafter.registry;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.quackimpala7321.crafter.screen.CrafterScreenHandler;

public class ModScreenHandlers {
    public static final DeferredRegister<ScreenHandlerType<?>> SCREEN_HANDLERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, "crafter");

    public static final RegistryObject<ScreenHandlerType<CrafterScreenHandler>> CRAFTER_3X3 = SCREEN_HANDLERS.register("crafter_3x3",
            () -> IForgeContainerType.create(CrafterScreenHandler::new));
}
