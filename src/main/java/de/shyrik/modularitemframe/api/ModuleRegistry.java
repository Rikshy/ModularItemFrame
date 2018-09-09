package de.shyrik.modularitemframe.api;


import de.shyrik.modularitemframe.common.item.ItemModule;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModuleRegistry {

    private static Map<ResourceLocation, Class<? extends ModuleBase>> modules = new HashMap<>();

    /**
     * registers a module class
     *
     * @param id          unique id
     * @param moduleClass module to register
     * @throws IllegalArgumentException when id is duplicated
     */
    public static void register(ResourceLocation id, Class<? extends ModuleBase> moduleClass) {
        if (get(id).isPresent())
            throw new IllegalArgumentException("[ModularItemFrame] module key already exists!");
        modules.put(id, moduleClass);
    }

    /**
     * Registers the module class and creates a new {@link ItemModule} instance
     *
     * @param id          unique id
     * @param moduleClass module to register
     * @return new {@link ItemModule} instance
     */
    public static ItemModule registerCreate(ResourceLocation id, Class<? extends ModuleBase> moduleClass) {
        register(id, moduleClass);
        return new ItemModule(id);
    }

    /**
     * creates an instance of a module
     *
     * @param id the id of the module
     * @return created instance
     */
    @Nullable
    public static ModuleBase createModuleInstance(ResourceLocation id) {
        Optional<Class<? extends ModuleBase>> mod = get(id);
        if (mod.isPresent()) {
            try {
                return modules.get(id).newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the module id of a specific module
     *
     * @param moduleClass module to look up
     * @return unique module id
     */
    public static ResourceLocation getModuleId(Class<? extends ModuleBase> moduleClass) {
        for (Map.Entry<ResourceLocation, Class<? extends ModuleBase>> entry : modules.entrySet())
            if (entry.getValue() == moduleClass) return entry.getKey();
        return null;
    }

    private static Optional<Class<? extends ModuleBase>> get(ResourceLocation id) {
        return modules.keySet().stream().filter(r -> r.toString().equals(id.toString())).findAny().map(modules::get);
    }
}
