package net.quackimpala7321.crafter.registry;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.quackimpala7321.crafter.CrafterMod;
import net.quackimpala7321.crafter.screen.CrafterScreenHandler;

public class ModScreenHandlers {
    public static final DeferredRegister<ScreenHandlerType<?>> SCREEN_HANDLERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CrafterMod.MOD_ID);

    public static final RegistryObject<ScreenHandlerType<CrafterScreenHandler>> CRAFTER_3X3 = SCREEN_HANDLERS.register("crafter_3x3",
            () -> IForgeMenuType.create(CrafterScreenHandler::new));
}
